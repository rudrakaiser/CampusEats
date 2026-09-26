package com.mad.campuseats.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Pink,
    onPrimary = Color.White,
    primaryContainer = PinkTint,
    onPrimaryContainer = PinkDark,
    secondary = Success,
    onSecondary = Color.White,
    background = Color.White,
    onBackground = Charcoal,
    surface = Color.White,
    onSurface = Charcoal,
    surfaceVariant = Sand,
    onSurfaceVariant = Ink,
    outline = Line,
    error = Danger
)

@Composable
fun CampusEatsTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = AppTypography,
        content = content
    )
}
