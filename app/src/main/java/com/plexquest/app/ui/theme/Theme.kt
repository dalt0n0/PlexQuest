package com.plexquest.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val PlexDarkColorScheme = darkColorScheme(
    primary = PlexOrange,
    onPrimary = PlexBackground,
    primaryContainer = PlexOrangeDark,
    secondary = PlexOrange,
    background = PlexBackground,
    surface = PlexSurface,
    surfaceVariant = PlexSurfaceVariant,
    onBackground = PlexOnSurface,
    onSurface = PlexOnSurface,
    onSurfaceVariant = PlexOnSurfaceVariant,
    error = PlexError,
)

@Composable
fun PlexQuestTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PlexDarkColorScheme,
        typography = PlexTypography,
        content = content,
    )
}
