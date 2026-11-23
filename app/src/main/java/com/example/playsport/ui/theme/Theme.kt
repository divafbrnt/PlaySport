package com.example.playsport.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Terang = lightColorScheme(
    primary = Color(0xFF156D46),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD8F3E4),
    onPrimaryContainer = Color(0xFF083E29),
    secondary = Color(0xFF4C6558),
    background = Color(0xFFF8FBF8),
    surface = Color.White,
    surfaceVariant = Color(0xFFE8F1EB),
    onSurface = Color(0xFF17261D)
)

private val Gelap = darkColorScheme(
    primary = Color(0xFF9BDBB3),
    onPrimary = Color(0xFF073B25),
    primaryContainer = Color(0xFF1B553A),
    onPrimaryContainer = Color(0xFFCAF5DC),
    background = Color(0xFF111C16),
    surface = Color(0xFF19281F),
    surfaceVariant = Color(0xFF24382D)
)

@Composable
fun PlaySportTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) Gelap else Terang,
        typography = Typography(),
        content = content
    )
}
