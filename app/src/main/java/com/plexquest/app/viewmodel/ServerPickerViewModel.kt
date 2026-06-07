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
import java.util.UUID
import javax.inject.Inject

data class ServerPickerState(
    val servers: List<PlexServer> = emptyList(),
    val isLoading: Boolean = false,
    val selectedServer: PlexServer? = null,
    val error: String? = null,
)

@HiltViewModel
class ServerPickerViewModel @Inject constructor(
    private val authApi: PlexAuthApi,
    private val preferences: PlexPreferences,
) : ViewModel() {

    private val _state = MutableStateFlow(ServerPickerState())
    val state = _state.asStateFlow()

    private val clientId = UUID.randomUUID().toString()

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
                val response = authApi.getResources(token, clientId)
                if (response.isSuccessful) {
                    val servers = response.body()
                        ?.filter { it.provides.contains("server") }
                        ?.flatMap { resource ->
                            resource.connections?.map { conn ->
                                PlexServer(
                                    name = resource.name,
                                    address = conn.uri.removePrefix("http://").removePrefix("https://").substringBefore(":"),
                                    port = conn.uri.substringAfterLast(":").toIntOrNull() ?: 32400,
                                    token = resource.accessToken ?: token,
                                    machineIdentifier = resource.machineIdentifier,
                                    local = conn.local,
                                )
                            } ?: emptyList()
                        }
                        // Prefer local connections first
                        ?.sortedByDescending { it.local }
                        ?: emptyList()

                    preferences.saveServers(servers)
                    _state.update { it.copy(isLoading = false, servers = servers) }
                } else {
                    _state.update { it.copy(isLoading = false, error = "Failed to load servers (${response.code()})") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Network error: ${e.message}") }
            }
        }
    }

    fun selectServer(server: PlexServer) {
        viewModelScope.launch {
            preferences.setActiveServer(server.machineIdentifier)
            _state.update { it.copy(selectedServer = server) }
        }
    }
}
