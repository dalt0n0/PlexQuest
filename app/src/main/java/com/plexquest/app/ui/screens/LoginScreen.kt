package com.plexquest.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.plexquest.app.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    onSignInWithPlex: () -> Unit,
    onLoginSuccess: () -> Unit,
    vm: LoginViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsState()

    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) onLoginSuccess()
    }

    // Token dialog (fallback for power users)
    if (state.showTokenDialog) {
        AlertDialog(
            onDismissRequest = vm::closeTokenDialog,
            title = { Text("Sign in with token") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Plex Web → Settings → Troubleshooting → \"Your account token\"",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    OutlinedTextField(
                        value = state.tokenInput,
                        onValueChange = vm::onTokenInputChange,
                        label = { Text("Plex token") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                }
            },
            confirmButton = {
                Button(
                    onClick = vm::signInWithToken,
                    enabled = state.tokenInput.isNotBlank() && !state.isLoading,
                ) {
                    if (state.isLoading) CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp)
                    else Text("Connect")
                }
            },
            dismissButton = {
                TextButton(onClick = vm::closeTokenDialog) { Text("Cancel") }
            },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "PlexQuest",
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            "Plex on Meta Quest",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(Modifier.height(56.dp))

        Button(
            onClick = onSignInWithPlex,
            modifier = Modifier
                .widthIn(min = 280.dp)
                .height(52.dp),
        ) {
            Text("Sign in with Plex", style = MaterialTheme.typography.labelLarge)
        }

        Spacer(Modifier.height(16.dp))

        TextButton(onClick = vm::openTokenDialog) {
            Text("Use auth token instead")
        }
    }
}
