package com.plexquest.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plexquest.app.data.models.MediaItem
import com.plexquest.app.data.models.MediaLibrary
import com.plexquest.app.data.models.PlexHub
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

data class HomeHub(
    val title: String,
    val items: List<MediaItem>,
)

data class HomeState(
    val libraries: List<MediaLibrary> = emptyList(),
    val hubs: List<HomeHub> = emptyList(),
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

            val server = activeServer() ?: run {
                _state.update { it.copy(isLoading = false, error = "No server selected") }
                return@launch
            }

            // Libraries for nav chips
            launch {
                val result = repository.getLibraries(server).first { it !is PlexResult.Loading }
                if (result is PlexResult.Success) _state.update { it.copy(libraries = result.data) }
            }

            // Home hubs — the primary content rows (Continue Watching, Recently Added, etc.)
            val hubResult = repository.getHomeHubs(server).first { it !is PlexResult.Loading }
            when (hubResult) {
                is PlexResult.Success -> {
                    val hubs = hubResult.data.map { hub ->
                        HomeHub(
                            title = hub.title,
                            items = hub.metadata?.map { meta ->
                                MediaItem(
                                    ratingKey = meta.ratingKey,
                                    title = meta.title,
                                    year = meta.year,
                                    summary = meta.summary,
                                    thumb = meta.thumb?.let { "${server.baseUrl}$it?X-Plex-Token=${server.token}" },
                                    art = meta.art?.let { "${server.baseUrl}$it?X-Plex-Token=${server.token}" },
                                    type = meta.type,
                                    duration = meta.duration,
                                    viewOffset = meta.viewOffset,
                                    grandparentTitle = meta.grandparentTitle,
                                    parentIndex = meta.parentIndex,
                                    index = meta.index,
                                    contentRating = meta.contentRating,
                                    rating = meta.rating,
                                    audienceRating = meta.audienceRating,
                                    addedAt = meta.addedAt,
                                )
                            } ?: emptyList(),
                        )
                    }
                    _state.update { it.copy(hubs = hubs, isLoading = false) }
                }
                is PlexResult.Error -> {
                    // Hubs not available (older Plex server?) — fall back to on-deck + recently added
                    fallbackLoad(server)
                }
                else -> {}
            }
        }
    }

    private suspend fun fallbackLoad(server: PlexServer) {
        val hubs = mutableListOf<HomeHub>()

        val deckResult = repository.getOnDeck(server).first { it !is PlexResult.Loading }
        if (deckResult is PlexResult.Success && deckResult.data.isNotEmpty()) {
            hubs.add(HomeHub("Continue Watching", deckResult.data))
        }

        val libResult = repository.getLibraries(server).first { it !is PlexResult.Loading }
        if (libResult is PlexResult.Success) {
            libResult.data.take(3).forEach { lib ->
                val recResult = repository.getRecentlyAdded(server, lib.key)
                    .first { it !is PlexResult.Loading }
                if (recResult is PlexResult.Success && recResult.data.isNotEmpty()) {
                    hubs.add(HomeHub("Recently Added · ${lib.title}", recResult.data))
                }
            }
        }

        _state.update { it.copy(hubs = hubs, isLoading = false) }
    }

    private suspend fun activeServer(): PlexServer? {
        val servers = preferences.servers.firstOrNull() ?: return null
        val activeId = preferences.activeServerId.firstOrNull()
        return servers.firstOrNull { it.machineIdentifier == activeId } ?: servers.firstOrNull()
    }
}
