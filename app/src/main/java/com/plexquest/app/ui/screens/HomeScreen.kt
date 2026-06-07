package com.plexquest.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.plexquest.app.ui.components.MediaCard
import com.plexquest.app.ui.components.SectionHeader
import com.plexquest.app.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLibraryClick: (sectionId: String, title: String) -> Unit,
    onMediaClick: (ratingKey: String) -> Unit,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    vm: HomeViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PlexQuest") },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Filled.Search, "Search")
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Filled.Person, "Settings")
                    }
                },
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading && state.hubs.isEmpty()) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
                return@Box
            }

            state.error?.let { err ->
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(err, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = vm::reload) { Text("Retry") }
                }
                return@Box
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
            ) {
                // Library chips
                if (state.libraries.isNotEmpty()) {
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(state.libraries) { lib ->
                                FilterChip(
                                    selected = false,
                                    onClick = { onLibraryClick(lib.key, lib.title) },
                                    label = {
                                        Text(lib.title, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    },
                                )
                            }
                        }
                    }
                }

                // Hub rows — one section per hub
                state.hubs.forEach { hub ->
                    item(key = hub.title) {
                        SectionHeader(hub.title)
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            items(hub.items, key = { it.ratingKey }) { item ->
                                MediaCard(
                                    item = item,
                                    modifier = Modifier
                                        .width(140.dp)
                                        .clickable { onMediaClick(item.ratingKey) },
                                )
                            }
                        }
                    }
                }

                if (state.hubs.isEmpty() && !state.isLoading) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                            Text("Nothing to show yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}
