package com.plexquest.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plexquest.app.data.api.PlexAuthApi
import com.plexquest.app.data.store.PlexPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginState(
    val tokenInput: String = "",
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val showTokenDialog: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authApi: PlexAuthApi,
    private val preferences: PlexPreferences,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun onTokenInputChange(value: String) = _state.update { it.copy(tokenInput = value, error = null) }
    fun openTokenDialog() = _state.update { it.copy(showTokenDialog = true, error = null) }
    fun closeTokenDialog() = _state.update { it.copy(showTokenDialog = false, tokenInput = "") }

    fun signInWithToken() {
        val token = _state.value.tokenInput.trim()
        if (token.isBlank()) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val response = authApi.getResources(token)
                if (response.isSuccessful) {
                    preferences.saveAuthInfo(token, "Plex User", null)
                    _state.update { it.copy(isLoading = false, isLoggedIn = true, showTokenDialog = false) }
                } else {
                    _state.update { it.copy(isLoading = false, error = "Invalid token (${response.code()})") }
                }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = "Network error: ${e.message}") }
            }
        }
    }
}
