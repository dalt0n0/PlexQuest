package com.plexquest.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Choose a Server") },
                actions = {
                    IconButton(onClick = vm::refresh) {
                        Icon(Icons.Filled.Refresh, "Refresh servers")
                    }
                },
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(16.dp))
                    Text("Discovering servers…", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            LazyColumn {
                items(state.servers) { server ->
                    ServerItem(server = server, onClick = { vm.selectServer(server) })
                }
            }

            state.error?.let { err ->
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(err, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = vm::refresh) { Text("Retry") }
                }
            }
        }
    }
}

@Composable
private fun ServerItem(server: PlexServer, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(server.name) },
        supportingContent = { Text("${server.address}:${server.port}") },
        leadingContent = { Icon(Icons.Filled.Computer, null) },
        trailingContent = {
            if (server.local) {
                Badge { Text("LAN") }
            }
        },
        modifier = Modifier.clickable(onClick = onClick),
    )
    HorizontalDivider()
}
