package com.carlauncher.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val CarDarkColorScheme = darkColorScheme(
    primary = BlueAccent,
    secondary = PurpleAccent,
    tertiary = GreenAccent,
    background = DarkBackground,
    surface = DarkSurface,
    surfaceVariant = DarkSurfaceVariant,
    onPrimary = TextPrimary,
    onSecondary = TextPrimary,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary,
    outline = DarkBorder,
)

@Composable
fun CarLauncherTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CarDarkColorScheme,
        typography = CarLauncherTypography,
        content = content,
    )
}
