package com.josephhopson.sprituum.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * App colors for light theme - refined warm cocktail-inspired palette
 */
private val LightColors = lightColorScheme(
    primary = Color(0xFF7D4F00),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDEAD),
    onPrimaryContainer = Color(0xFF291800),
    secondary = Color(0xFF795D40),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDCC1),
    onSecondaryContainer = Color(0xFF2C1600),
    tertiary = Color(0xFF656032),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFEBE4AA),
    onTertiaryContainer = Color(0xFF1F1C00),
    error = Color(0xFFBA1A1A),
    errorContainer = Color(0xFFFFDAD6),
    onError = Color.White,
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFFF8F5),
    onBackground = Color(0xFF201A17),
    surface = Color(0xFFFFF8F5),
    onSurface = Color(0xFF201A17),
    surfaceVariant = Color(0xFFF4DED4),
    onSurfaceVariant = Color(0xFF53443D),
    outline = Color(0xFF9C8579),
    outlineVariant = Color(0xFFD8C2B7)
)

/**
 * App colors for dark theme - refined warm cocktail-inspired palette
 */
private val DarkColors = darkColorScheme(
    primary = Color(0xFFFFB95C),
    onPrimary = Color(0xFF462900),
    primaryContainer = Color(0xFF633E00),
    onPrimaryContainer = Color(0xFFFFDEAD),
    secondary = Color(0xFFE5BFA8),
    onSecondary = Color(0xFF432C1A),
    secondaryContainer = Color(0xFF5D432D),
    onSecondaryContainer = Color(0xFFFFDCC1),
    tertiary = Color(0xFFCFC890),
    onTertiary = Color(0xFF353107),
    tertiaryContainer = Color(0xFF4C481C),
    onTertiaryContainer = Color(0xFFEBE4AA),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    onError = Color(0xFF690005),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF201A17),
    onBackground = Color(0xFFEDE0D4),
    surface = Color(0xFF201A17),
    onSurface = Color(0xFFEDE0D4),
    surfaceVariant = Color(0xFF53443D),
    onSurfaceVariant = Color(0xFFD7C2BA),
    outline = Color(0xFF9F8D85),
    outlineVariant = Color(0xFF5C4739)
)

/**
 * Typography for the Cocktail Recipe app
 */
private val AppTypography = Typography(
    displayLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
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
        typography = AppTypography,
        content = content
    )
}
