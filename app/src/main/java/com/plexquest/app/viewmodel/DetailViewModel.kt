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
                    val item = with(repository) { metaResult.data.toMediaItem(server) }
                    _state.update { it.copy(item = item) }

                    if (metaResult.data.type == "show" || metaResult.data.type == "season") {
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
