package com.plexquest.app.ui.screens

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.ui.PlayerView
import com.plexquest.app.viewmodel.PlayerViewModel
import kotlinx.coroutines.delay

@Composable
fun PlayerScreen(
    ratingKey: String,
    onBack: () -> Unit,
    vm: PlayerViewModel = hiltViewModel(),
) {
    LaunchedEffect(ratingKey) { vm.load(ratingKey) }

    val state by vm.state.collectAsState()
    var controlsVisible by remember { mutableStateOf(true) }

    LaunchedEffect(controlsVisible, state.isPlaying) {
        if (controlsVisible && state.isPlaying) {
            delay(3000)
            controlsVisible = false
        }
    }

    // Error state — show over black background with back button
    if (state.error != null) {
        Box(Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(state.error!!, color = Color.White)
                Button(onClick = onBack) { Text("Go back") }
            }
        }
        return
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable { controlsVisible = !controlsVisible },
    ) {
        // ExoPlayer surface
        AndroidView(
            factory = { context ->
                PlayerView(context).apply {
                    useController = false
                    layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                    )
                }
            },
            update = { playerView ->
                playerView.player = vm.player
            },
            modifier = Modifier.fillMaxSize(),
        )

        // Loading overlay
        if (state.isBuffering) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        // Controls overlay
        AnimatedVisibility(
            visible = controlsVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f)),
            ) {
                // Top bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .align(Alignment.TopCenter),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                    }
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            state.title,
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                        )
                        state.subtitle?.let { sub ->
                            Text(sub, color = Color.White.copy(alpha = 0.7f), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }

                // Center playback controls
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = { vm.seekRelative(-10_000L) }, modifier = Modifier.size(56.dp)) {
                        Icon(Icons.Filled.Replay10, "Rewind 10s", tint = Color.White, modifier = Modifier.size(40.dp))
                    }
                    FilledIconButton(
                        onClick = vm::togglePlayPause,
                        modifier = Modifier.size(72.dp),
                    ) {
                        Icon(
                            if (state.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (state.isPlaying) "Pause" else "Play",
                            modifier = Modifier.size(40.dp),
                        )
                    }
                    IconButton(onClick = { vm.seekRelative(10_000L) }, modifier = Modifier.size(56.dp)) {
                        Icon(Icons.Filled.Forward10, "Forward 10s", tint = Color.White, modifier = Modifier.size(40.dp))
                    }
                }

                // Bottom progress bar
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                ) {
                    Slider(
                        value = if (state.duration > 0) state.position.toFloat() / state.duration else 0f,
                        onValueChange = { fraction -> vm.seekTo((fraction * state.duration).toLong()) },
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                        ),
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(state.position.formatDuration(), color = Color.White, style = MaterialTheme.typography.labelSmall)
                        Text(state.duration.formatDuration(), color = Color.White, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}

private fun Long.formatDuration(): String {
    val totalSeconds = this / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) "%d:%02d:%02d".format(hours, minutes, seconds)
    else "%d:%02d".format(minutes, seconds)
}
