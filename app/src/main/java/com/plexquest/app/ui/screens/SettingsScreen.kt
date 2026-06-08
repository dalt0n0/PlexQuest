package com.plexquest.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.plexquest.app.data.models.PlexServer
import com.plexquest.app.viewmodel.SettingsViewModel

// Quality options shown in the picker
private data class QualityOption(val label: String, val kbps: Int)
private val QUALITY_OPTIONS = listOf(
    QualityOption("Original (Direct Play)", Int.MAX_VALUE),
    QualityOption("40 Mbps  (4K / Best)", 40_000),
    QualityOption("20 Mbps  (1080p High)", 20_000),
    QualityOption("8 Mbps   (1080p)", 8_000),
    QualityOption("4 Mbps   (720p)", 4_000),
    QualityOption("2 Mbps   (480p)", 2_000),
    QualityOption("1 Mbps   (360p)", 1_000),
)

private val AUDIO_LANG_OPTIONS = listOf(
    "" to "Auto (server default)",
    "en" to "English",
    "es" to "Spanish",
    "fr" to "French",
    "de" to "German",
    "ja" to "Japanese",
    "ko" to "Korean",
    "zh" to "Chinese",
    "pt" to "Portuguese",
    "it" to "Italian",
)

private val SUBTITLE_LANG_OPTIONS = listOf(
    "" to "Off",
    "en" to "English",
    "es" to "Spanish",
    "fr" to "French",
    "de" to "German",
    "ja" to "Japanese",
    "ko" to "Korean",
    "zh" to "Chinese",
    "pt" to "Portuguese",
    "it" to "Italian",
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLoggedOut: () -> Unit,
    vm: SettingsViewModel = hiltViewModel(),
) {
    val state by vm.state.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showQualityDialog by remember { mutableStateOf(false) }
    var showAudioLangDialog by remember { mutableStateOf(false) }
    var showSubtitleLangDialog by remember { mutableStateOf(false) }

    // ── Dialogs ──────────────────────────────────────────────────────────────

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Sign out?") },
            text = { Text("You'll need to sign in again and re-select a server.") },
            confirmButton = {
                Button(
                    onClick = { vm.logout(onLoggedOut) },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                ) { Text("Sign out") }
            },
            dismissButton = { TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") } },
        )
    }

    if (showQualityDialog) {
        SimplePickerDialog(
            title = "Video Quality",
            options = QUALITY_OPTIONS.map { it.label to (it.kbps == state.videoQualityKbps) },
            onSelect = { idx ->
                vm.setVideoQuality(QUALITY_OPTIONS[idx].kbps)
                vm.setDirectPlay(QUALITY_OPTIONS[idx].kbps == Int.MAX_VALUE)
                showQualityDialog = false
            },
            onDismiss = { showQualityDialog = false },
        )
    }

    if (showAudioLangDialog) {
        SimplePickerDialog(
            title = "Default Audio Language",
            options = AUDIO_LANG_OPTIONS.map { it.second to (it.first == state.preferredAudioLang) },
            onSelect = { idx ->
                vm.setPreferredAudioLang(AUDIO_LANG_OPTIONS[idx].first)
                showAudioLangDialog = false
            },
            onDismiss = { showAudioLangDialog = false },
        )
    }

    if (showSubtitleLangDialog) {
        SimplePickerDialog(
            title = "Default Subtitles",
            options = SUBTITLE_LANG_OPTIONS.map { it.second to (it.first == state.preferredSubtitleLang) },
            onSelect = { idx ->
                vm.setPreferredSubtitleLang(SUBTITLE_LANG_OPTIONS[idx].first)
                showSubtitleLangDialog = false
            },
            onDismiss = { showSubtitleLangDialog = false },
        )
    }

    // ── Layout ───────────────────────────────────────────────────────────────

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
            )
        }
    ) { padding ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {

            // ── Account ──────────────────────────────────────────────────────
            item { SectionHeader("Account") }
            item {
                ListItem(
                    headlineContent = { Text(state.username) },
                    supportingContent = { Text("Signed in to Plex") },
                    leadingContent = {
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(44.dp),
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    state.username.take(1).uppercase(),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    style = MaterialTheme.typography.titleLarge,
                                )
                            }
                        }
                    },
                    trailingContent = {
                        TextButton(
                            onClick = { showLogoutDialog = true },
                            colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ExitToApp, null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Sign out")
                        }
                    },
                )
                HorizontalDivider()
            }

            // ── Servers ──────────────────────────────────────────────────────
            if (state.servers.isNotEmpty()) {
                item { SectionHeader("Servers") }
                items(state.servers) { server ->
                    ServerRow(
                        server = server,
                        isActive = server.machineIdentifier == state.activeServerId,
                        onSelect = { vm.switchServer(server) },
                    )
                }
                item { HorizontalDivider(modifier = Modifier.padding(top = 4.dp)) }
            }

            // ── Playback ─────────────────────────────────────────────────────
            item { SectionHeader("Playback") }

            item {
                ListItem(
                    headlineContent = { Text("Video Quality") },
                    supportingContent = {
                        val label = QUALITY_OPTIONS.firstOrNull { it.kbps == state.videoQualityKbps }?.label
                            ?: "8 Mbps (1080p)"
                        Text(label)
                    },
                    leadingContent = { Icon(Icons.Filled.HighQuality, null) },
                    trailingContent = { Icon(Icons.Filled.ChevronRight, null) },
                    modifier = Modifier.clickable { showQualityDialog = true },
                )
                HorizontalDivider()
            }

            item {
                ListItem(
                    headlineContent = { Text("Direct Play") },
                    supportingContent = { Text("Stream original file without re-encoding") },
                    leadingContent = { Icon(Icons.Filled.PlayCircle, null) },
                    trailingContent = {
                        Switch(
                            checked = state.directPlayEnabled,
                            onCheckedChange = { vm.setDirectPlay(it) },
                        )
                    },
                    modifier = Modifier.clickable { vm.setDirectPlay(!state.directPlayEnabled) },
                )
                HorizontalDivider()
            }

            item {
                ListItem(
                    headlineContent = { Text("Auto-Play Next Episode") },
                    supportingContent = { Text("Automatically start the next episode when done") },
                    leadingContent = { Icon(Icons.Filled.SkipNext, null) },
                    trailingContent = {
                        Switch(
                            checked = state.autoPlayNext,
                            onCheckedChange = { vm.setAutoPlayNext(it) },
                        )
                    },
                    modifier = Modifier.clickable { vm.setAutoPlayNext(!state.autoPlayNext) },
                )
                HorizontalDivider()
            }

            // ── Audio & Subtitles ─────────────────────────────────────────────
            item { SectionHeader("Audio & Subtitles") }

            item {
                val audioLabel = AUDIO_LANG_OPTIONS.firstOrNull { it.first == state.preferredAudioLang }?.second
                    ?: "Auto"
                ListItem(
                    headlineContent = { Text("Default Audio Language") },
                    supportingContent = { Text(audioLabel) },
                    leadingContent = { Icon(Icons.Filled.Audiotrack, null) },
                    trailingContent = { Icon(Icons.Filled.ChevronRight, null) },
                    modifier = Modifier.clickable { showAudioLangDialog = true },
                )
                HorizontalDivider()
            }

            item {
                val subLabel = SUBTITLE_LANG_OPTIONS.firstOrNull { it.first == state.preferredSubtitleLang }?.second
                    ?: "Off"
                ListItem(
                    headlineContent = { Text("Default Subtitles") },
                    supportingContent = { Text(subLabel) },
                    leadingContent = { Icon(Icons.Filled.Subtitles, null) },
                    trailingContent = { Icon(Icons.Filled.ChevronRight, null) },
                    modifier = Modifier.clickable { showSubtitleLangDialog = true },
                )
                HorizontalDivider()
            }

            // ── About ─────────────────────────────────────────────────────────
            item { SectionHeader("About") }
            item {
                ListItem(
                    headlineContent = { Text("PlexQuest") },
                    supportingContent = { Text("v0.1.0  ·  Open source Plex client for Meta Quest") },
                    leadingContent = { Icon(Icons.Filled.Info, null) },
                )
                HorizontalDivider()
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}

// ── Reusable composables ──────────────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 16.dp, top = 20.dp, bottom = 4.dp),
    )
}

@Composable
private fun ServerRow(server: PlexServer, isActive: Boolean, onSelect: () -> Unit) {
    ListItem(
        headlineContent = { Text(server.name) },
        supportingContent = { Text("${server.address}:${server.port}  ${if (server.local) "· LAN" else "· Remote"}") },
        leadingContent = { Icon(Icons.Filled.Computer, null) },
        trailingContent = {
            if (isActive) {
                Icon(Icons.Filled.CheckCircle, "Active", tint = MaterialTheme.colorScheme.primary)
            } else {
                TextButton(onClick = onSelect) { Text("Use") }
            }
        },
    )
    HorizontalDivider()
}

@Composable
private fun SimplePickerDialog(
    title: String,
    options: List<Pair<String, Boolean>>,
    onSelect: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                options.forEachIndexed { idx, (label, selected) ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(idx) }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(selected = selected, onClick = { onSelect(idx) })
                        Spacer(Modifier.width(8.dp))
                        Text(label, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}
