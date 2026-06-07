package com.plexquest.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.plexquest.app.data.models.MediaItem
import com.plexquest.app.ui.components.MediaCard
import com.plexquest.app.ui.components.SectionHeader
import com.plexquest.app.viewmodel.DetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    ratingKey: String,
    onPlay: (ratingKey: String) -> Unit,
    onChildClick: (ratingKey: String) -> Unit,
    onBack: () -> Unit,
    vm: DetailViewModel = hiltViewModel(),
) {
    LaunchedEffect(ratingKey) { vm.load(ratingKey) }
    val state by vm.state.collectAsState()
    val item = state.item

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(item?.title ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
                return@Box
            }
            state.error?.let { err ->
                Text(err, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center).padding(16.dp))
                return@Box
            }
            item ?: return@Box

            LazyColumn(Modifier.fillMaxSize()) {
                // Backdrop art
                item {
                    AsyncImage(
                        model = item.art ?: item.thumb,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                    )
                }

                // Poster + info row
                item {
                    Row(Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        AsyncImage(
                            model = item.thumb,
                            contentDescription = item.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.width(100.dp).height(150.dp).clip(RoundedCornerShape(8.dp)),
                        )
                        Column(Modifier.weight(1f)) {
                            Text(item.title, style = MaterialTheme.typography.headlineMedium)
                            item.year?.let { Text("$it", color = MaterialTheme.colorScheme.onSurfaceVariant) }
                            item.contentRating?.let { Text(it, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                            item.audienceRating?.let {
                                Text("★ %.1f".format(it), color = MaterialTheme.colorScheme.primary)
                            }
                            Spacer(Modifier.height(8.dp))

                            // Play button — only for directly playable types
                            if (item.type == "movie" || item.type == "episode") {
                                Button(
                                    onClick = { onPlay(item.ratingKey) },
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Icon(Icons.Filled.PlayArrow, null)
                                    Spacer(Modifier.width(4.dp))
                                    Text(if (item.progressPercent > 0f) "Resume" else "Play")
                                }
                            }
                        }
                    }
                }

                // Summary
                item.summary?.takeIf { it.isNotBlank() }?.let { summary ->
                    item {
                        Text(
                            summary,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        )
                    }
                }

                // Children (seasons or episodes)
                if (state.children.isNotEmpty()) {
                    val label = when (item.type) {
                        "show" -> "Seasons"
                        "season" -> "Episodes"
                        else -> "More"
                    }
                    item { SectionHeader(label) }

                    if (item.type == "show") {
                        // Seasons as horizontal row of cards
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                items(state.children, key = { it.ratingKey }) { child ->
                                    MediaCard(
                                        item = child,
                                        modifier = Modifier.width(120.dp).clickable { onChildClick(child.ratingKey) },
                                    )
                                }
                            }
                        }
                    } else {
                        // Episodes as vertical list
                        items(state.children, key = { it.ratingKey }) { episode ->
                            EpisodeItem(episode = episode, onClick = { onPlay(episode.ratingKey) })
                        }
                    }
                }

                item { Spacer(Modifier.height(32.dp)) }
            }
        }
    }
}

@Composable
private fun EpisodeItem(episode: MediaItem, onClick: () -> Unit) {
    ListItem(
        headlineContent = {
            Text(
                text = buildString {
                    episode.index?.let { append("E$it · ") }
                    append(episode.title)
                },
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        supportingContent = episode.summary?.takeIf { it.isNotBlank() }?.let { sum ->
            { Text(sum, maxLines = 2, overflow = TextOverflow.Ellipsis) }
        },
        trailingContent = {
            if (episode.progressPercent > 0f && !episode.isWatched) {
                CircularProgressIndicator(
                    progress = { episode.progressPercent },
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 3.dp,
                )
            } else if (episode.isWatched) {
                Icon(Icons.Filled.CheckCircle, null, tint = MaterialTheme.colorScheme.primary)
            }
        },
        modifier = Modifier.clickable(onClick = onClick),
    )
    HorizontalDivider()
}
