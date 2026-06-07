package com.plexquest.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem as Media3Item
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.plexquest.app.data.models.PlexServer
import com.plexquest.app.data.repository.PlexRepository
import com.plexquest.app.data.repository.PlexResult
import com.plexquest.app.data.store.PlexPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class PlayerState(
    val title: String = "",
    val subtitle: String? = null,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = true,
    val position: Long = 0L,
    val duration: Long = 0L,
    val error: String? = null,
    val isTranscoding: Boolean = false,
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val repository: PlexRepository,
    private val preferences: PlexPreferences,
) : ViewModel() {

    val player: ExoPlayer = ExoPlayer.Builder(context).build()
    private val _state = MutableStateFlow(PlayerState())
    val state = _state.asStateFlow()

    private var currentRatingKey: String? = null
    private var currentPartKey: String? = null
    private var scrobbleJob: Job? = null
    private val clientId = UUID.randomUUID().toString()

    init {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _state.update { it.copy(isPlaying = isPlaying) }
                if (isPlaying) startScrobbling() else stopScrobbling()
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                _state.update { it.copy(isBuffering = playbackState == Player.STATE_BUFFERING) }
                if (playbackState == Player.STATE_ENDED) {
                    reportCurrentTimeline("stopped")
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                val isTranscoding = _state.value.isTranscoding
                if (!isTranscoding) {
                    // Direct play failed — try transcoder
                    tryTranscode()
                } else {
                    _state.update { it.copy(error = "Playback failed: ${error.message}") }
                }
            }
        })

        viewModelScope.launch {
            while (isActive) {
                _state.update { it.copy(
                    position = player.currentPosition,
                    duration = player.duration.coerceAtLeast(0L),
                )}
                delay(500)
            }
        }
    }

    fun load(ratingKey: String) {
        viewModelScope.launch {
            _state.update { it.copy(isBuffering = true, error = null, isTranscoding = false) }
            val server = activeServer() ?: run {
                _state.update { it.copy(error = "No server configured") }
                return@launch
            }

            val result = repository.getMetadata(server, ratingKey)
                .first { it !is PlexResult.Loading }

            when (result) {
                is PlexResult.Error -> _state.update { it.copy(error = result.message) }
                is PlexResult.Success -> {
                    val meta = result.data
                    val partKey = meta.media?.firstOrNull()?.parts?.firstOrNull()?.key
                    if (partKey == null) {
                        _state.update { it.copy(error = "No playable media found") }
                        return@launch
                    }

                    currentRatingKey = meta.ratingKey
                    currentPartKey = partKey

                    val subtitle = when {
                        meta.grandparentTitle != null && meta.parentIndex != null && meta.index != null ->
                            "${meta.grandparentTitle} · S${meta.parentIndex}E${meta.index}"
                        meta.year != null -> meta.year.toString()
                        else -> null
                    }
                    _state.update { it.copy(title = meta.title, subtitle = subtitle) }

                    val streamUrl = repository.buildStreamUrl(server, partKey)
                    val resumeMs = meta.viewOffset ?: 0L
                    playUrl(streamUrl, resumeMs)
                }
                else -> {}
            }
        }
    }

    private fun tryTranscode() {
        viewModelScope.launch {
            val server = activeServer() ?: return@launch
            val ratingKey = currentRatingKey ?: return@launch
            val partKey = currentPartKey ?: return@launch
            _state.update { it.copy(isTranscoding = true, error = null) }
            val transcodeUrl = repository.buildTranscodeUrl(server, ratingKey, partKey, clientId)
            playUrl(transcodeUrl, player.currentPosition)
        }
    }

    private fun playUrl(url: String, resumeMs: Long) {
        val mediaItem = Media3Item.fromUri(url)
        player.setMediaItem(mediaItem, resumeMs)
        player.prepare()
        player.play()
    }

    fun togglePlayPause() {
        if (player.isPlaying) player.pause() else player.play()
    }

    fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
        _state.update { it.copy(position = positionMs) }
    }

    fun seekRelative(deltaMs: Long) {
        val target = (player.currentPosition + deltaMs).coerceIn(0L, player.duration)
        player.seekTo(target)
    }

    private fun startScrobbling() {
        scrobbleJob?.cancel()
        scrobbleJob = viewModelScope.launch {
            while (isActive) {
                delay(10_000)
                reportCurrentTimeline("playing")
            }
        }
    }

    private fun stopScrobbling() {
        scrobbleJob?.cancel()
        reportCurrentTimeline("paused")
    }

    private fun reportCurrentTimeline(state: String) {
        val ratingKey = currentRatingKey ?: return
        val partKey = currentPartKey ?: return
        viewModelScope.launch {
            val server = activeServer() ?: return@launch
            repository.reportTimeline(
                server = server,
                ratingKey = ratingKey,
                partKey = partKey,
                state = state,
                positionMs = player.currentPosition,
                durationMs = player.duration.coerceAtLeast(0L),
            )
        }
    }

    private suspend fun activeServer(): PlexServer? {
        val servers = preferences.servers.firstOrNull() ?: return null
        val activeId = preferences.activeServerId.firstOrNull()
        return servers.firstOrNull { it.machineIdentifier == activeId } ?: servers.firstOrNull()
    }

    override fun onCleared() {
        reportCurrentTimeline("stopped")
        player.release()
        super.onCleared()
    }
}
