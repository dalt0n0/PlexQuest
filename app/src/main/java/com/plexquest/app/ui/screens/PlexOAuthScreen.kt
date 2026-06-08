package com.plexquest.app.ui.screens

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.plexquest.app.viewmodel.OAuthState
import com.plexquest.app.viewmodel.OAuthViewModel

@Composable
fun PlexOAuthScreen(
    onSuccess: () -> Unit,
    onCancel: () -> Unit,
    vm: OAuthViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsState()

    LaunchedEffect(Unit) { vm.start() }

    LaunchedEffect(state) {
        if (state is OAuthState.Success) onSuccess()
    }

    when (val s = state) {
        is OAuthState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    CircularProgressIndicator()
                    Text("Connecting to Plex…", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        is OAuthState.Browser -> {
            Column(Modifier.fillMaxSize()) {
                // Thin top bar with cancel
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Filled.Close, contentDescription = "Cancel")
                    }
                    Text(
                        "Sign in with your Plex account",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(start = 4.dp),
                    )
                }
                HorizontalDivider()

                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.setSupportZoom(false)
                            webViewClient = object : WebViewClient() {
                                override fun shouldOverrideUrlLoading(
                                    view: WebView,
                                    request: WebResourceRequest,
                                ) = false // let the WebView handle all navigation
                            }
                            loadUrl(s.url)
                        }
                    },
                    update = { /* url only set once; polling ViewModel handles token */ },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        is OAuthState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.padding(32.dp),
                ) {
                    Text(s.message, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = onCancel) { Text("Cancel") }
                        Button(onClick = vm::retry) { Text("Retry") }
                    }
                }
            }
        }

        else -> {} // Idle / Success handled via LaunchedEffect
    }
}
