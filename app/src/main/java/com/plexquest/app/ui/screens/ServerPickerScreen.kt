package com.plexquest.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.plexquest.app.data.models.PlexServer
import com.plexquest.app.viewmodel.ServerPickerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerPickerScreen(
    onServerSelected: () -> Unit,
    vm: ServerPickerViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsState()

    LaunchedEffect(state.selectedServer) {
        if (state.selectedServer != null) onServerSelected()
    }

    // Manual add dialog
    if (state.showAddDialog) {
        AlertDialog(
            onDismissRequest = vm::closeAddDialog,
            title = { Text("Add server manually") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = state.manualHost,
                        onValueChange = vm::onManualHostChange,
                        label = { Text("IP address or hostname") },
                        placeholder = { Text("192.168.1.x") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    OutlinedTextField(
                        value = state.manualPort,
                        onValueChange = vm::onManualPortChange,
                        label = { Text("Port") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                    )
                    state.manualError?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall) }
                }
            },
            confirmButton = {
                Button(
                    onClick = vm::addManualServer,
                    enabled = state.manualHost.isNotBlank(),
                ) { Text("Add") }
            },
            dismissButton = {
                TextButton(onClick = vm::closeAddDialog) { Text("Cancel") }
            },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Choose a Server") },
                actions = {
                    IconButton(onClick = vm::refresh) {
                        Icon(Icons.Filled.Refresh, "Refresh")
                    }
                    IconButton(onClick = vm::openAddDialog) {
                        Icon(Icons.Filled.Add, "Add server manually")
                    }
                },
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        CircularProgressIndicator()
                        Text("Discovering servers…", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                state.servers.isNotEmpty() -> {
                    LazyColumn(Modifier.fillMaxSize()) {
                        items(state.servers) { server ->
                            ServerItem(server = server, onClick = { vm.selectServer(server) })
                        }
                        item {
                            // Always offer manual add at bottom of list
                            ListItem(
                                headlineContent = { Text("Add server manually…") },
                                leadingContent = { Icon(Icons.Filled.Add, null) },
                                modifier = Modifier.clickable(onClick = vm::openAddDialog),
                            )
                        }
                    }

                    state.error?.let { err ->
                        Snackbar(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(16.dp)
                        ) { Text(err) }
                    }
                }

                else -> {
                    // Empty + maybe error
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Text(
                            state.error ?: "No servers found on your account.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedButton(onClick = vm::refresh) { Text("Retry") }
                            Button(onClick = vm::openAddDialog) { Text("Add manually") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ServerItem(server: PlexServer, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(server.name) },
        supportingContent = { Text(server.baseUrl) },
        leadingContent = { Icon(Icons.Filled.Computer, null) },
        trailingContent = {
            if (server.local) Badge { Text("LAN") }
        },
        modifier = Modifier.clickable(onClick = onClick),
    )
    HorizontalDivider()
}
