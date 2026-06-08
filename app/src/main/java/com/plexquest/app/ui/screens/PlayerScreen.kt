package com.plexquest.app.ui.screens

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.ui.PlayerView
import com.plexquest.app.viewmodel.PlayerViewModel
import com.plexquest.app.viewmodel.TrackOption
import kotlinx.coroutines.delay

private val SPEEDS = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)

@Composable
fun PlayerScreen(
    ratingKey: String,
    onBack: () -> Unit,
    vm: PlayerViewModel = hiltViewModel(),
) {
    LaunchedEffect(ratingKey) { vm.load(ratingKey) }

    val state by vm.state.collectAsState()
    var controlsVisible by remember { mutableStateOf(true) }

    // Auto-hide controls after 4s of playing
    LaunchedEffect(controlsVisible, state.isPlaying) {
        if (controlsVisible && state.isPlaying) {
            delay(4_000)
            controlsVisible = false
        }
    }

    // ── Pickers ─────────────────────────────────────────────────────────────

    if (state.showAudioPicker) {
        TrackPickerDialog(
            title = "Audio Track",
            tracks = state.audioTracks,
            onSelect = vm::selectAudioTrack,
            onDismiss = vm::dismissAudioPicker,
        )
    }

    if (state.showSubtitlePicker) {
        TrackPickerDialog(
            title = "Subtitles",
            tracks = state.subtitleTracks,
            onSelect = vm::selectSubtitleTrack,
            onDismiss = vm::dismissSubtitlePicker,
        )
    }

    if (state.showSpeedPicker) {
        SpeedPickerDialog(
            currentSpeed = state.playbackSpeed,
            onSelect = vm::setSpeed,
            onDismiss = vm::dismissSpeedPicker,
        )
    }

    // ── Error ────────────────────────────────────────────────────────────────

    if (state.error != null) {
        Box(Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(state.error!!, color = Color.White)
                Button(onClick = onBack) { Text("Go back") }
            }
        }
        return
    }

    // ── Main ─────────────────────────────────────────────────────────────────

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { controlsVisible = !controlsVisible },
    ) {
        // Video surface
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    useController = false
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                }
            },
            update = { it.player = vm.player },
            modifier = Modifier.fillMaxSize(),
        )

        // Buffering spinner
        if (state.isBuffering) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.White,
            )
        }

        if (state.isTranscoding) {
            Badge(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp),
                containerColor = MaterialTheme.colorScheme.primary,
            ) { Text("HLS") }
        }

        // ── Controls overlay ─────────────────────────────────────────────────
        AnimatedVisibility(
            visible = controlsVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
            ) {

                // TOP BAR — back, title, track/speed buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .align(Alignment.TopCenter),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                    Column(Modifier.weight(1f).padding(start = 4.dp)) {
                        Text(state.title, color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        state.subtitle?.let { Text(it, color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodySmall) }
                    }

                    // Audio track
                    if (state.audioTracks.isNotEmpty()) {
                        IconButton(onClick = vm::showAudioPicker) {
                            Icon(Icons.Filled.Audiotrack, "Audio track", tint = Color.White)
                        }
                    }
                    // Subtitles
                    IconButton(onClick = vm::showSubtitlePicker) {
                        Icon(Icons.Filled.Subtitles, "Subtitles", tint = Color.White)
                    }
                    // Speed
                    IconButton(onClick = vm::showSpeedPicker) {
                        Icon(Icons.Filled.Speed, "Playback speed", tint = Color.White)
                    }
                }

                // CENTER — skip / play / skip
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = { vm.seekRelative(-30_000L) },
                        modifier = Modifier.size(56.dp),
                    ) {
                        Icon(Icons.Filled.Replay30, "Rewind 30s", tint = Color.White, modifier = Modifier.size(40.dp))
                    }
                    FilledIconButton(
                        onClick = vm::togglePlayPause,
                        modifier = Modifier.size(72.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = Color.White.copy(alpha = 0.15f),
                        ),
                    ) {
                        Icon(
                            if (state.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (state.isPlaying) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(44.dp),
                        )
                    }
                    IconButton(
                        onClick = { vm.seekRelative(30_000L) },
                        modifier = Modifier.size(56.dp),
                    ) {
                        Icon(Icons.Filled.Forward30, "Forward 30s", tint = Color.White, modifier = Modifier.size(40.dp))
                    }
                }

                // BOTTOM — progress bar + time
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                ) {
                    // Speed badge
                    if (state.playbackSpeed != 1.0f) {
                        Text(
                            "${state.playbackSpeed}×",
                            color = Color.White.copy(alpha = 0.7f),
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.align(Alignment.End).padding(bottom = 2.dp),
                        )
                    }

                    Slider(
                        value = if (state.duration > 0) state.position.toFloat() / state.duration else 0f,
                        onValueChange = { vm.seekTo((it * state.duration).toLong()) },
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = Color.White.copy(alpha = 0.3f),
                        ),
                    )
                    Row(
                        Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(state.position.fmtDuration(), color = Color.White, style = MaterialTheme.typography.labelSmall)
                        Text(state.duration.fmtDuration(), color = Color.White, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

// ── Dialogs ───────────────────────────────────────────────────────────────────

@Composable
private fun TrackPickerDialog(
    title: String,
    tracks: List<TrackOption>,
    onSelect: (TrackOption) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.large,
            tonalElevation = 8.dp,
            modifier = Modifier.widthIn(min = 280.dp, max = 400.dp),
        ) {
            Column {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                )
                HorizontalDivider()
                LazyColumn(modifier = Modifier.heightIn(max = 360.dp)) {
                    items(tracks) { track ->
                        ListItem(
                            headlineContent = { Text(track.label) },
                            leadingContent = {
                                RadioButton(
                                    selected = track.isSelected,
                                    onClick = { onSelect(track) },
                                )
                            },
                            modifier = Modifier.clickable { onSelect(track) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SpeedPickerDialog(
    currentSpeed: Float,
    onSelect: (Float) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.large,
            tonalElevation = 8.dp,
            modifier = Modifier.widthIn(min = 260.dp, max = 360.dp),
        ) {
            Column {
                Text(
                    "Playback Speed",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                )
                HorizontalDivider()
                SPEEDS.forEach { speed ->
                    ListItem(
                        headlineContent = {
                            Text(if (speed == 1.0f) "Normal (1×)" else "${speed}×")
                        },
                        leadingContent = {
                            RadioButton(
                                selected = currentSpeed == speed,
                                onClick = { onSelect(speed) },
                            )
                        },
                        modifier = Modifier.clickable { onSelect(speed) },
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
}

private fun Long.fmtDuration(): String {
    val s = this / 1000
    val h = s / 3600
    val m = (s % 3600) / 60
    val sec = s % 60
    return if (h > 0) "%d:%02d:%02d".format(h, m, sec) else "%d:%02d".format(m, sec)
}
