package com.josephhopson.sprituum.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * App colors for light theme
 */
private val LightColors = lightColorScheme(
    primary = Color(0xFF924100),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDBCA),
    onPrimaryContainer = Color(0xFF311300),
    secondary = Color(0xFF765849),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDBCA),
    onSecondaryContainer = Color(0xFF2B160B),
    tertiary = Color(0xFF656032),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFEBE4AA),
    onTertiaryContainer = Color(0xFF1F1C00),
    error = Color(0xFFBA1A1A),
    errorContainer = Color(0xFFFFDAD6),
    onError = Color.White,
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFFFBFF),
    onBackground = Color(0xFF201A17),
    surface = Color(0xFFFFFBFF),
    onSurface = Color(0xFF201A17),
    surfaceVariant = Color(0xFFF4DED4),
    onSurfaceVariant = Color(0xFF53443D),
    outline = Color(0xFF85736B)
)

/**
 * App colors for dark theme
 */
private val DarkColors = darkColorScheme(
    primary = Color(0xFFFFB690),
    onPrimary = Color(0xFF522200),
    primaryContainer = Color(0xFF753100),
    onPrimaryContainer = Color(0xFFFFDBCA),
    secondary = Color(0xFFE6BEAC),
    onSecondary = Color(0xFF432B1E),
    secondaryContainer = Color(0xFF5D4133),
    onSecondaryContainer = Color(0xFFFFDBCA),
    tertiary = Color(0xFFCFC890),
    onTertiary = Color(0xFF353107),
    tertiaryContainer = Color(0xFF4C481C),
    onTertiaryContainer = Color(0xFFEBE4AA),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onError = Color(0xFF690005),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF201A17),
    onBackground = Color(0xFFECE0DB),
    surface = Color(0xFF201A17),
    onSurface = Color(0xFFECE0DB),
    surfaceVariant = Color(0xFF53443D),
    onSurfaceVariant = Color(0xFFD7C2BA),
    outline = Color(0xFF9F8D85)
)

/**
 * App theme for Cocktail Recipe app with Material 3 theming
 */
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}