package com.plexquest.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plexquest.app.data.models.MediaItem
import com.plexquest.app.data.models.MediaLibrary
import com.plexquest.app.data.models.PlexServer
import com.plexquest.app.data.repository.PlexRepository
import com.plexquest.app.data.repository.PlexResult
import com.plexquest.app.data.store.PlexPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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

    fun reload() = load()

    private fun load() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val server = activeServer()
            if (server == null) {
                _state.update { it.copy(isLoading = false, error = "No server selected") }
                return@launch
            }

            // Libraries
            val libResult = repository.getLibraries(server).first { it !is PlexResult.Loading }
            val libs = when (libResult) {
                is PlexResult.Success -> libResult.data.also {
                    _state.update { s -> s.copy(libraries = it) }
                }
                is PlexResult.Error -> {
                    _state.update { it.copy(isLoading = false, error = libResult.message) }
                    return@launch
                }
                else -> return@launch
            }

            // Global on-deck (Continue Watching across all libraries)
            launch {
                val result = repository.getOnDeck(server).first { it !is PlexResult.Loading }
                if (result is PlexResult.Success) {
                    _state.update { it.copy(onDeck = result.data) }
                }
            }

            // Recently Added from first library
            launch {
                val firstLib = libs.firstOrNull() ?: return@launch
                val result = repository.getRecentlyAdded(server, firstLib.key)
                    .first { it !is PlexResult.Loading }
                if (result is PlexResult.Success) {
                    _state.update { it.copy(recentlyAdded = result.data) }
                }
            }

            _state.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun activeServer(): PlexServer? {
        val servers = preferences.servers.firstOrNull() ?: return null
        val activeId = preferences.activeServerId.firstOrNull()
        return servers.firstOrNull { it.machineIdentifier == activeId } ?: servers.firstOrNull()
    }
}
