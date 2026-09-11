package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val VipDarkColorScheme = darkColorScheme(
    primary = AccentPurple,
    onPrimary = VoidBlack,
    primaryContainer = RoyalPurple,
    onPrimaryContainer = TextPrimary,
    secondary = HighlightPurple,
    onSecondary = VoidBlack,
    secondaryContainer = DarkPurple,
    onSecondaryContainer = TextPrimary,
    tertiary = GoldAccent,
    onTertiary = Color(0xFF1E1B00),
    background = VoidBlack,
    onBackground = TextPrimary,
    surface = DarkPurple,
    onSurface = TextPrimary,
    surfaceVariant = DarkPurple,
    onSurfaceVariant = TextSecondary,
    outline = RoyalPurple,
    outlineVariant = RoyalPurple.copy(alpha = 0.5f),
    error = StopRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = VipDarkColorScheme,
        typography = Typography,
        content = content
    )
}
