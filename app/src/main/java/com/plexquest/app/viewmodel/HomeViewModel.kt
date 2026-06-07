package com.plexquest.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plexquest.app.data.models.MediaItem
import com.plexquest.app.data.models.MediaLibrary
import com.plexquest.app.data.repository.PlexRepository
import com.plexquest.app.data.repository.PlexResult
import com.plexquest.app.data.store.PlexPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeState(
    val libraries: List<MediaLibrary> = emptyList(),
    val onDeck: List<MediaItem> = emptyList(),
    val recentlyAdded: List<MediaItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: PlexRepository,
    private val preferences: PlexPreferences,
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init { load() }

    private fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val servers = preferences.servers.firstOrNull() ?: emptyList()
            val activeId = preferences.activeServerId.firstOrNull()
            val server = servers.firstOrNull { it.machineIdentifier == activeId }
                ?: servers.firstOrNull()

            if (server == null) {
                _state.update { it.copy(isLoading = false, error = "No server selected") }
                return@launch
            }

            // Load libraries
            repository.getLibraries(server).collect { result ->
                when (result) {
                    is PlexResult.Success -> {
                        _state.update { it.copy(libraries = result.data) }
                        // Load on-deck and recently added from first library
                        result.data.firstOrNull()?.let { lib ->
                            loadOnDeck(server, lib.key)
                            loadRecentlyAdded(server, lib.key)
                        }
                    }
                    is PlexResult.Error -> _state.update { it.copy(error = result.message) }
                    PlexResult.Loading -> {}
                }
            }

            _state.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun loadOnDeck(server: com.plexquest.app.data.models.PlexServer, sectionId: String) {
        repository.getOnDeck(server, sectionId).collect { result ->
            if (result is PlexResult.Success) {
                _state.update { it.copy(onDeck = result.data) }
            }
        }
    }

    private suspend fun loadRecentlyAdded(server: com.plexquest.app.data.models.PlexServer, sectionId: String) {
        repository.getRecentlyAdded(server, sectionId).collect { result ->
            if (result is PlexResult.Success) {
                _state.update { it.copy(recentlyAdded = result.data) }
            }
        }
    }
}
