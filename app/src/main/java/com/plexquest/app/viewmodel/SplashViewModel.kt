package com.plexquest.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plexquest.app.data.store.PlexPreferences
import com.plexquest.app.ui.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(preferences: PlexPreferences) : ViewModel() {

    // null = still loading
    val startDestination = combine(
        preferences.authToken,
        preferences.servers,
    ) { token, servers ->
        when {
            token.isNullOrBlank() -> Routes.LOGIN
            servers.isEmpty() -> Routes.SERVER_PICKER
            else -> Routes.HOME
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)
}
