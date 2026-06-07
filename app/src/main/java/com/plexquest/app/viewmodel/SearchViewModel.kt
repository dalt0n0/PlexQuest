package com.plexquest.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plexquest.app.data.models.MediaItem
import com.plexquest.app.data.repository.PlexRepository
import com.plexquest.app.data.repository.PlexResult
import com.plexquest.app.data.store.PlexPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchState(
    val query: String = "",
    val results: List<MediaItem> = emptyList(),
    val isLoading: Boolean = false,
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: PlexRepository,
    private val preferences: PlexPreferences,
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state = _state.asStateFlow()
    private var searchJob: Job? = null

    fun onQueryChange(query: String) {
        _state.update { it.copy(query = query) }
        searchJob?.cancel()
        if (query.isBlank()) {
            _state.update { it.copy(results = emptyList()) }
            return
        }
        searchJob = viewModelScope.launch {
            delay(300) // debounce
            performSearch(query)
        }
    }

    private suspend fun performSearch(query: String) {
        val servers = preferences.servers.firstOrNull() ?: return
        val activeId = preferences.activeServerId.firstOrNull()
        val server = servers.firstOrNull { it.machineIdentifier == activeId } ?: servers.firstOrNull() ?: return

        _state.update { it.copy(isLoading = true) }
        repository.search(server, query).collect { result ->
            when (result) {
                is PlexResult.Success -> _state.update { it.copy(isLoading = false, results = result.data) }
                is PlexResult.Error -> _state.update { it.copy(isLoading = false) }
                PlexResult.Loading -> {}
            }
        }
    }
}
