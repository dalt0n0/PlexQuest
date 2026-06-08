package com.plexquest.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plexquest.app.data.api.PlexAuthApi
import com.plexquest.app.data.models.PlexServer
import com.plexquest.app.data.store.PlexPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ServerPickerState(
    val servers: List<PlexServer> = emptyList(),
    val isLoading: Boolean = false,
    val selectedServer: PlexServer? = null,
    val error: String? = null,
    val showAddDialog: Boolean = false,
    val manualHost: String = "",
    val manualPort: String = "32400",
    val manualError: String? = null,
)

@HiltViewModel
class ServerPickerViewModel @Inject constructor(
    private val authApi: PlexAuthApi,
    private val preferences: PlexPreferences,
) : ViewModel() {

    private val _state = MutableStateFlow(ServerPickerState())
    val state = _state.asStateFlow()

    init { refresh() }

    fun refresh() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val token = preferences.authToken.firstOrNull()
            if (token == null) {
                _state.update { it.copy(isLoading = false, error = "Not signed in") }
                return@launch
            }
            try {
                val response = authApi.getResources(token)
                if (response.isSuccessful) {
                    val servers = response.body()
                        ?.filter { it.provides.contains("server") }
                        ?.flatMap { resource ->
                            (resource.connections ?: emptyList())
                                .filter { !it.relay }  // skip relay-only connections
                                .map { conn ->
                                    PlexServer(
                                        name = resource.name,
                                        baseUrl = conn.uri.trimEnd('/'),
                                        token = resource.accessToken ?: token,
                                        machineIdentifier = resource.machineIdentifier,
                                        local = conn.local,
                                    )
                                }
                        }
                        ?.sortedWith(compareByDescending<PlexServer> { it.local }.thenBy { it.name })
                        ?: emptyList()

                    // Merge with any previously saved manual servers
                    val saved = preferences.servers.firstOrNull() ?: emptyList()
                    val manual = saved.filter { s -> servers.none { it.machineIdentifier == s.machineIdentifier } }
                    val merged = servers + manual

                    preferences.saveServers(merged)
                    _state.update { it.copy(isLoading = false, servers = merged) }
                } else {
                    val saved = preferences.servers.firstOrNull() ?: emptyList()
                    _state.update {
                        it.copy(
                            isLoading = false,
                            servers = saved,
                            error = if (saved.isEmpty()) "Discovery failed (${response.code()}). Add server manually." else null,
                        )
                    }
                }
            } catch (e: Exception) {
                val saved = preferences.servers.firstOrNull() ?: emptyList()
                _state.update {
                    it.copy(
                        isLoading = false,
                        servers = saved,
                        error = if (saved.isEmpty()) "Network error. Add server manually." else null,
                    )
                }
            }
        }
    }

    fun selectServer(server: PlexServer) {
        viewModelScope.launch {
            preferences.setActiveServer(server.machineIdentifier)
            _state.update { it.copy(selectedServer = server) }
        }
    }

    fun openAddDialog() = _state.update { it.copy(showAddDialog = true, manualHost = "", manualPort = "32400", manualError = null) }
    fun closeAddDialog() = _state.update { it.copy(showAddDialog = false) }
    fun onManualHostChange(v: String) = _state.update { it.copy(manualHost = v, manualError = null) }
    fun onManualPortChange(v: String) = _state.update { it.copy(manualPort = v, manualError = null) }

    fun addManualServer() {
        val host = _state.value.manualHost.trim()
        val port = _state.value.manualPort.trim().toIntOrNull()
        if (host.isBlank()) {
            _state.update { it.copy(manualError = "Enter an IP address or hostname") }
            return
        }
        if (port == null || port !in 1..65535) {
            _state.update { it.copy(manualError = "Invalid port") }
            return
        }
        viewModelScope.launch {
            val token = preferences.authToken.firstOrNull() ?: return@launch
            val baseUrl = "http://$host:$port"
            val server = PlexServer(
                name = host,
                baseUrl = baseUrl,
                token = token,
                machineIdentifier = "manual-$host-$port",
                local = true,
            )
            val updated = _state.value.servers + server
            preferences.saveServers(updated)
            _state.update { it.copy(servers = updated, showAddDialog = false) }
        }
    }
}
