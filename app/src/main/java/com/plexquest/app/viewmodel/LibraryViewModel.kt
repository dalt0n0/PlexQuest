package com.plexquest.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plexquest.app.data.models.MediaItem
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

data class LibraryState(
    val items: List<MediaItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: PlexRepository,
    private val preferences: PlexPreferences,
) : ViewModel() {

    private val _state = MutableStateFlow(LibraryState())
    val state = _state.asStateFlow()

    fun load(sectionId: String) {
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

            repository.getLibraryContents(server, sectionId).collect { result ->
                when (result) {
                    is PlexResult.Success -> _state.update { it.copy(isLoading = false, items = result.data) }
                    is PlexResult.Error -> _state.update { it.copy(isLoading = false, error = result.message) }
                    PlexResult.Loading -> {}
                }
            }
        }
    }
}
