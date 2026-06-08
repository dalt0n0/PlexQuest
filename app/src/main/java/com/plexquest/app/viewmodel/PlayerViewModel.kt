package com.plexquest.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem as Media3Item
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.Tracks
import androidx.media3.common.TrackSelectionOverride
import com.plexquest.app.PlexConstants
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
import java.util.Locale
import javax.inject.Inject
import androidx.media3.exoplayer.ExoPlayer

data class TrackOption(
    val groupIndex: Int,
    val trackIndex: Int,
    val label: String,
    val isSelected: Boolean,
)

data class PlayerState(
    val title: String = "",
    val subtitle: String? = null,
    val isPlaying: Boolean = false,
    val isBuffering: Boolean = true,
    val position: Long = 0L,
    val duration: Long = 0L,
    val error: String? = null,
    val isTranscoding: Boolean = false,
    val audioTracks: List<TrackOption> = emptyList(),
    val subtitleTracks: List<TrackOption> = emptyList(),
    val playbackSpeed: Float = 1.0f,
    val showAudioPicker: Boolean = false,
    val showSubtitlePicker: Boolean = false,
    val showSpeedPicker: Boolean = false,
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val repository: PlexRepository,
    private val preferences: PlexPreferences,
) : ViewModel() {

    val player: ExoPlayer = ExoPlayer.Builder(context)
        .setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(C.USAGE_MEDIA)
                .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
                .build(),
            /* handleAudioFocus= */ true,
        )
        .build()

    private val _state = MutableStateFlow(PlayerState())
    val state = _state.asStateFlow()

    private var currentRatingKey: String? = null
    private var currentPartKey: String? = null
    private var scrobbleJob: Job? = null

    init {
        player.volume = 1.0f

        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _state.update { it.copy(isPlaying = isPlaying) }
                if (isPlaying) startScrobbling() else stopScrobbling()
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                _state.update { it.copy(isBuffering = playbackState == Player.STATE_BUFFERING) }
                if (playbackState == Player.STATE_ENDED) reportCurrentTimeline("stopped")
            }

            override fun onPlayerError(error: PlaybackException) {
                if (!_state.value.isTranscoding) tryTranscode()
                else _state.update { it.copy(error = "Playback failed: ${error.message}") }
            }

            override fun onTracksChanged(tracks: Tracks) {
                updateTracks(tracks)
            }
        })

        // Position polling
        viewModelScope.launch {
            while (isActive) {
                _state.update {
                    it.copy(
                        position = player.currentPosition,
                        duration = player.duration.coerceAtLeast(0L),
                    )
                }
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
            val directPlay = preferences.directPlayEnabled.firstOrNull() ?: true
            val audioLang = preferences.preferredAudioLang.firstOrNull() ?: ""
            val subtitleLang = preferences.preferredSubtitleLang.firstOrNull() ?: ""

            // Apply language preferences to track selection
            if (audioLang.isNotEmpty()) {
                player.trackSelectionParameters = player.trackSelectionParameters.buildUpon()
                    .setPreferredAudioLanguage(audioLang)
                    .build()
            }
            if (subtitleLang.isNotEmpty()) {
                player.trackSelectionParameters = player.trackSelectionParameters.buildUpon()
                    .setPreferredTextLanguage(subtitleLang)
                    .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, false)
                    .build()
            } else {
                player.trackSelectionParameters = player.trackSelectionParameters.buildUpon()
                    .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, true)
                    .build()
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

                    val resumeMs = meta.viewOffset ?: 0L
                    if (directPlay) {
                        playUrl(repository.buildStreamUrl(server, partKey), resumeMs)
                    } else {
                        val qualityKbps = preferences.videoQualityKbps.firstOrNull() ?: 8_000
                        _state.update { it.copy(isTranscoding = true) }
                        playUrl(repository.buildTranscodeUrl(server, ratingKey, partKey, qualityKbps), resumeMs)
                    }
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
            val qualityKbps = preferences.videoQualityKbps.firstOrNull() ?: 8_000
            _state.update { it.copy(isTranscoding = true, error = null) }
            playUrl(repository.buildTranscodeUrl(server, ratingKey, partKey, qualityKbps), player.currentPosition)
        }
    }

    private fun playUrl(url: String, resumeMs: Long) {
        player.setMediaItem(Media3Item.fromUri(url), resumeMs)
        player.prepare()
        player.play()
    }

    // ── Track selection ──────────────────────────────────────────────────────

    private fun updateTracks(tracks: Tracks) {
        val audio = mutableListOf<TrackOption>()
        val subs = mutableListOf(
            TrackOption(-1, -1, "Off", player.trackSelectionParameters.disabledTrackTypes.contains(C.TRACK_TYPE_TEXT))
        )

        tracks.groups.forEachIndexed { gi, group ->
            when (group.type) {
                C.TRACK_TYPE_AUDIO -> {
                    for (ti in 0 until group.length) {
                        val fmt = group.getTrackFormat(ti)
                        val lang = fmt.language?.let {
                            runCatching { Locale(it).displayLanguage }.getOrDefault(it)
                        } ?: "Track ${gi + 1}"
                        val channels = if ((fmt.channelCount) > 0) " · ${fmt.channelCount}ch" else ""
                        val codec = fmt.sampleMimeType?.removePrefix("audio/")?.uppercase() ?: ""
                        val label = if (codec.isNotEmpty()) "$lang  $codec$channels" else "$lang$channels"
                        audio.add(TrackOption(gi, ti, label, group.isTrackSelected(ti)))
                    }
                }
                C.TRACK_TYPE_TEXT -> {
                    for (ti in 0 until group.length) {
                        val fmt = group.getTrackFormat(ti)
                        val lang = fmt.language?.let {
                            runCatching { Locale(it).displayLanguage }.getOrDefault(it)
                        } ?: "Subtitle ${gi + 1}"
                        val forced = if (fmt.selectionFlags and C.SELECTION_FLAG_FORCED != 0) " (forced)" else ""
                        subs.add(TrackOption(gi, ti, "$lang$forced", group.isTrackSelected(ti)))
                    }
                }
            }
        }

        // Mark "Off" as selected only if subtitles are actually disabled
        val subsDisabled = player.trackSelectionParameters.disabledTrackTypes.contains(C.TRACK_TYPE_TEXT)
        subs[0] = subs[0].copy(isSelected = subsDisabled && subs.drop(1).none { it.isSelected })

        _state.update { it.copy(audioTracks = audio, subtitleTracks = subs) }
    }

    fun selectAudioTrack(option: TrackOption) {
        val group = player.currentTracks.groups.getOrNull(option.groupIndex) ?: return
        player.trackSelectionParameters = player.trackSelectionParameters.buildUpon()
            .clearOverridesOfType(C.TRACK_TYPE_AUDIO)
            .addOverride(TrackSelectionOverride(group.mediaTrackGroup, option.trackIndex))
            .build()
        _state.update { it.copy(showAudioPicker = false) }
    }

    fun selectSubtitleTrack(option: TrackOption) {
        if (option.groupIndex == -1) {
            // "Off"
            player.trackSelectionParameters = player.trackSelectionParameters.buildUpon()
                .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, true)
                .build()
        } else {
            val group = player.currentTracks.groups.getOrNull(option.groupIndex) ?: return
            player.trackSelectionParameters = player.trackSelectionParameters.buildUpon()
                .setTrackTypeDisabled(C.TRACK_TYPE_TEXT, false)
                .clearOverridesOfType(C.TRACK_TYPE_TEXT)
                .addOverride(TrackSelectionOverride(group.mediaTrackGroup, option.trackIndex))
                .build()
        }
        updateTracks(player.currentTracks)
        _state.update { it.copy(showSubtitlePicker = false) }
    }

    fun setSpeed(speed: Float) {
        player.playbackParameters = PlaybackParameters(speed)
        _state.update { it.copy(playbackSpeed = speed, showSpeedPicker = false) }
    }

    fun showAudioPicker() = _state.update { it.copy(showAudioPicker = true) }
    fun dismissAudioPicker() = _state.update { it.copy(showAudioPicker = false) }
    fun showSubtitlePicker() = _state.update { it.copy(showSubtitlePicker = true) }
    fun dismissSubtitlePicker() = _state.update { it.copy(showSubtitlePicker = false) }
    fun showSpeedPicker() = _state.update { it.copy(showSpeedPicker = true) }
    fun dismissSpeedPicker() = _state.update { it.copy(showSpeedPicker = false) }

    // ── Playback controls ───────────────────────────────────────────────────

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

    // ── Scrobbling ──────────────────────────────────────────────────────────

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
