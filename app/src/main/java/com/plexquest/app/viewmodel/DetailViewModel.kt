package com.plexquest.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plexquest.app.data.models.MediaItem
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

data class DetailState(
    val item: MediaItem? = null,
    val children: List<MediaItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: PlexRepository,
    private val preferences: PlexPreferences,
) : ViewModel() {

    private val _state = MutableStateFlow(DetailState())
    val state = _state.asStateFlow()

    fun load(ratingKey: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val server = activeServer() ?: run {
                _state.update { it.copy(isLoading = false, error = "No server") }
                return@launch
            }

            val metaResult = repository.getMetadata(server, ratingKey)
                .first { it !is PlexResult.Loading }

            when (metaResult) {
                is PlexResult.Error -> {
                    _state.update { it.copy(isLoading = false, error = metaResult.message) }
                    return@launch
                }
                is PlexResult.Success -> {
                    val meta = metaResult.data
                    val item = MediaItem(
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
                    _state.update { it.copy(item = item) }

                    // Load children for shows (seasons) and seasons (episodes)
                    if (meta.type == "show" || meta.type == "season") {
                        val childResult = repository.getChildren(server, ratingKey)
                            .first { it !is PlexResult.Loading }
                        if (childResult is PlexResult.Success) {
                            _state.update { it.copy(children = childResult.data) }
                        }
                    }
                }
                else -> {}
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
