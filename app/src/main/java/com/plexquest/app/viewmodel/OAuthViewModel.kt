package com.plexquest.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plexquest.app.PlexConstants
import com.plexquest.app.data.api.PlexAuthApi
import com.plexquest.app.data.store.PlexPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.URLEncoder
import javax.inject.Inject

sealed class OAuthState {
    data object Idle : OAuthState()
    data object Loading : OAuthState()
    data class Browser(val url: String) : OAuthState()
    data class Error(val message: String) : OAuthState()
    data object Success : OAuthState()
}

@HiltViewModel
class OAuthViewModel @Inject constructor(
    private val authApi: PlexAuthApi,
    private val preferences: PlexPreferences,
) : ViewModel() {

    private val _state = MutableStateFlow<OAuthState>(OAuthState.Idle)
    val state = _state.asStateFlow()

    fun start() {
        if (_state.value is OAuthState.Browser || _state.value is OAuthState.Loading) return
        viewModelScope.launch {
            _state.value = OAuthState.Loading
            try {
                val pinResp = authApi.createPin()
                if (!pinResp.isSuccessful) {
                    _state.value = OAuthState.Error("Plex PIN request failed (${pinResp.code()})")
                    return@launch
                }
                val pin = pinResp.body()!!
                _state.value = OAuthState.Browser(buildAuthUrl(pin.code))
                pollForToken(pin.id)
            } catch (e: Exception) {
                _state.value = OAuthState.Error("Network error: ${e.message}")
            }
        }
    }

    fun retry() {
        _state.value = OAuthState.Idle
        start()
    }

    private fun buildAuthUrl(code: String): String {
        val encode: (String) -> String = { URLEncoder.encode(it, "UTF-8") }
        return "https://app.plex.tv/auth#?" +
            "clientID=${PlexConstants.CLIENT_ID}" +
            "&code=$code" +
            "&context%5Bdevice%5D%5Bproduct%5D=${encode("PlexQuest")}" +
            "&context%5Bdevice%5D%5Bplatform%5D=${encode("Android")}" +
            "&context%5Bdevice%5D%5Bdevice%5D=${encode("Meta Quest")}" +
            "&context%5Bdevice%5D%5BdeviceName%5D=${encode("PlexQuest")}"
    }

    private fun pollForToken(pinId: Long) {
        viewModelScope.launch {
            // Poll every 2s for up to 5 minutes
            repeat(150) {
                delay(2_000)
                try {
                    val resp = authApi.checkPin(pinId)
                    if (resp.isSuccessful) {
                        val token = resp.body()?.authToken
                        if (token != null) {
                            preferences.saveAuthInfo(token, "Plex User", null)
                            _state.value = OAuthState.Success
                            return@launch
                        }
                    }
                } catch (_: Exception) {}
            }
            if (_state.value !is OAuthState.Success) {
                _state.value = OAuthState.Error("Sign-in timed out. Tap retry.")
            }
        }
    }
}
