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
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferences: PlexPreferences,
) : ViewModel() {

    val state: StateFlow<SettingsState> = combine(
        preferences.username,
        preferences.servers,
        preferences.activeServerId,
    ) { username, servers, activeId ->
        SettingsState(
            username = username ?: "Plex User",
            servers = servers,
            activeServerId = activeId,
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, SettingsState())

    fun switchServer(server: PlexServer) {
        viewModelScope.launch { preferences.setActiveServer(server.machineIdentifier) }
    }

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            preferences.clearAuth()
            onDone()
        }
    }
}
