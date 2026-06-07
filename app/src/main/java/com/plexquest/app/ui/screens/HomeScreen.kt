package com.plexquest.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.plexquest.app.data.models.MediaItem
import com.plexquest.app.ui.components.MediaCard
import com.plexquest.app.ui.components.SectionHeader
import com.plexquest.app.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLibraryClick: (sectionId: String, title: String) -> Unit,
    onMediaClick: (ratingKey: String) -> Unit,
    onSearchClick: () -> Unit,
    vm: HomeViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PlexQuest") },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Filled.Search, contentDescription = "Search")
                    }
                },
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Libraries nav row
            if (state.libraries.isNotEmpty()) {
                item {
                    SectionHeader("Libraries")
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(state.libraries) { lib ->
                            FilterChip(
                                selected = false,
                                onClick = { onLibraryClick(lib.key, lib.title) },
                                label = { Text(lib.title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                            )
                        }
                    }
                }
            }

            // On Deck
            if (state.onDeck.isNotEmpty()) {
                item { SectionHeader("Continue Watching") }
                item {
                    MediaRow(items = state.onDeck, onMediaClick = onMediaClick)
                }
            }

            // Recently Added
            if (state.recentlyAdded.isNotEmpty()) {
                item { SectionHeader("Recently Added") }
                item {
                    MediaRow(items = state.recentlyAdded, onMediaClick = onMediaClick)
                }
            }

            if (state.isLoading) {
                item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            }

            state.error?.let { err ->
                item {
                    Text(
                        err,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun MediaRow(items: List<MediaItem>, onMediaClick: (String) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(items, key = { it.ratingKey }) { item ->
            MediaCard(
                item = item,
                modifier = Modifier
                    .width(140.dp)
                    .clickable { onMediaClick(item.ratingKey) },
            )
        }
    }
}
