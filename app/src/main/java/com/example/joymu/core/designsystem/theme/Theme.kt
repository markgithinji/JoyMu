package com.example.joymu.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = JoyGreenDarkPrimary,
    primaryContainer = JoyGreenOnContainer,
    secondary = JoyTerracottaSecondary,
    tertiary = JoyGoldTertiary,
    background = JoyBackgroundDark,
    surface = JoySurfaceDark,
    onPrimary = JoyGreenOnContainer,
    onBackground = JoyOnSurfaceDark,
    onSurface = JoyOnSurfaceDark
)

private val LightColorScheme = lightColorScheme(
    primary = JoyGreenPrimary,
    onPrimary = JoyGreenOnPrimary,
    primaryContainer = JoyGreenContainer,
    onPrimaryContainer = JoyGreenOnContainer,
    secondary = JoyTerracottaSecondary,
    secondaryContainer = JoyTerracottaContainer,
    tertiary = JoyGoldTertiary,
    tertiaryContainer = JoyGoldContainer,
    background = JoyBackgroundLight,
    surface = JoySurfaceLight,
    onBackground = JoyOnSurfaceLight,
    onSurface = JoyOnSurfaceLight,
    surfaceVariant = JoySurfaceVariant
)

@Composable
fun JoyMuTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
