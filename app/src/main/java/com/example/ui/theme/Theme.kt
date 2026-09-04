package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = IndigoPrimaryDark,
    onPrimary = Color(0xFF1E1B4B),
    primaryContainer = IndigoDarkContainer,
    onPrimaryContainer = Color(0xFFE0E7FF),
    secondary = Color(0xFFE2E8F0),
    onSecondary = Color(0xFF0F172A),
    secondaryContainer = Color(0xFF1E293B),
    onSecondaryContainer = Color(0xFFF1F5F9),
    tertiary = AnomalyAmberIcon,
    onTertiary = Color.White,
    background = DarkCanvasBg,
    onBackground = DarkTextMain,
    surface = DarkSurfaceCard,
    onSurface = DarkTextMain,
    surfaceVariant = Color(0xFF1E293B),
    onSurfaceVariant = DarkTextMuted,
    outline = DarkBorderLine,
    error = StatusError,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = IndigoPrimary,
    onPrimary = Color.White,
    primaryContainer = IndigoLightContainer,
    onPrimaryContainer = IndigoText,
    secondary = SlateButtonDark,
    onSecondary = Color.White,
    secondaryContainer = IndigoLightContainer,
    onSecondaryContainer = IndigoPrimary,
    tertiary = AnomalyAmberIcon,
    onTertiary = Color.White,
    tertiaryContainer = AnomalyAmberBg,
    onTertiaryContainer = AnomalyAmberText,
    background = CleanCanvasBg,
    onBackground = SlateTextPrimary,
    surface = CleanSurface,
    onSurface = SlateTextPrimary,
    surfaceVariant = CleanSurfaceVariant,
    onSurfaceVariant = SlateTextSecondary,
    outline = CleanBorder,
    outlineVariant = CleanBorderSubtle,
    error = StatusError,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
