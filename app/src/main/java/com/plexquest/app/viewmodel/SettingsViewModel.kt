package com.plexquest.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plexquest.app.data.models.PlexServer
import com.plexquest.app.data.store.PlexPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsState(
    val username: String = "",
    val servers: List<PlexServer> = emptyList(),
    val activeServerId: String? = null,
    val videoQualityKbps: Int = 8_000,
    val directPlayEnabled: Boolean = true,
    val autoPlayNext: Boolean = false,
    val preferredAudioLang: String = "",
    val preferredSubtitleLang: String = "",
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferences: PlexPreferences,
) : ViewModel() {

    val state: StateFlow<SettingsState> = combine(
        preferences.username,
        preferences.servers,
        preferences.activeServerId,
        preferences.videoQualityKbps,
        preferences.directPlayEnabled,
    ) { username, servers, activeId, quality, directPlay ->
        SettingsState(
            username = username ?: "Plex User",
            servers = servers,
            activeServerId = activeId,
            videoQualityKbps = quality,
            directPlayEnabled = directPlay,
        )
    }.combine(
        combine(preferences.autoPlayNext, preferences.preferredAudioLang, preferences.preferredSubtitleLang) {
            auto, audio, sub -> Triple(auto, audio, sub)
        }
    ) { s, (auto, audio, sub) ->
        s.copy(autoPlayNext = auto, preferredAudioLang = audio, preferredSubtitleLang = sub)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, SettingsState())

    fun switchServer(server: PlexServer) =
        viewModelScope.launch { preferences.setActiveServer(server.machineIdentifier) }

    fun setVideoQuality(kbps: Int) =
        viewModelScope.launch { preferences.setVideoQuality(kbps) }

    fun setDirectPlay(enabled: Boolean) =
        viewModelScope.launch { preferences.setDirectPlay(enabled) }

    fun setAutoPlayNext(enabled: Boolean) =
        viewModelScope.launch { preferences.setAutoPlayNext(enabled) }

    fun setPreferredAudioLang(lang: String) =
        viewModelScope.launch { preferences.setPreferredAudioLang(lang) }

    fun setPreferredSubtitleLang(lang: String) =
        viewModelScope.launch { preferences.setPreferredSubtitleLang(lang) }

    fun logout(onDone: () -> Unit) =
        viewModelScope.launch { preferences.clearAuth(); onDone() }
}
