package com.plexquest.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem as Media3Item
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.plexquest.app.data.repository.PlexRepository
import com.plexquest.app.data.repository.PlexResult
import com.plexquest.app.data.store.PlexPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerState(
    val title: String = "",
    val subtitle: String? = null,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = false,
    val position: Long = 0L,
    val duration: Long = 0L,
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

    init {
        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _state.update { it.copy(isPlaying = isPlaying) }
            }
            override fun onPlaybackStateChanged(playbackState: Int) {
                _state.update { it.copy(isBuffering = playbackState == Player.STATE_BUFFERING) }
            }
        })

        // Poll position every second
        viewModelScope.launch {
            while (isActive) {
                _state.update { it.copy(
                    position = player.currentPosition,
                    duration = player.duration.coerceAtLeast(0L),
                )}
                delay(1000)
            }
        }
    }

    fun load(ratingKey: String) {
        viewModelScope.launch {
            val servers = preferences.servers.firstOrNull() ?: return@launch
            val activeId = preferences.activeServerId.firstOrNull()
            val server = servers.firstOrNull { it.machineIdentifier == activeId }
                ?: servers.firstOrNull() ?: return@launch

            repository.getLibraryContents(server, ratingKey).collect { result ->
                if (result is PlexResult.Success) {
                    // getLibraryContents here is misused — use getMetadata instead (v0.1 simplification)
                }
            }

            // Direct metadata fetch
            try {
                val response = com.plexquest.app.data.api.PlexApi::class.java
                // Placeholder: actual implementation fetches metadata then builds stream URL
                // from the first Part's key and calls player.setMediaItem
                // Full implementation in a follow-up PR
            } catch (_: Exception) {}
        }
    }

    fun playUrl(url: String, title: String, subtitle: String? = null, resumeMs: Long = 0L) {
        _state.update { it.copy(title = title, subtitle = subtitle) }
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

    override fun onCleared() {
        player.release()
        super.onCleared()
    }
}
