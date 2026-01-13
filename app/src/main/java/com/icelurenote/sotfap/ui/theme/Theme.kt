package com.icelurenote.sotfap.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = IceBlue,
    onPrimary = SnowWhite,
    primaryContainer = GlacierBlue,
    onPrimaryContainer = DarkText,
    
    secondary = WinterMint,
    onSecondary = SnowWhite,
    secondaryContainer = CrystalBlue,
    onSecondaryContainer = DarkText,
    
    tertiary = DeepWater,
    onTertiary = SnowWhite,
    tertiaryContainer = IceShadow,
    onTertiaryContainer = DarkText,
    
    background = BackgroundLight,
    onBackground = DarkText,
    
    surface = SurfaceLight,
    onSurface = DarkText,
    surfaceVariant = FrostWhite,
    onSurfaceVariant = LightText,
    
    surfaceTint = IceBlue,
    inverseSurface = DarkText,
    inverseOnSurface = FrostWhite,
    
    error = DangerRed,
    onError = SnowWhite,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    
    outline = ColdGray,
    outlineVariant = IceShadow,
    scrim = Color(0xFF000000)
)

@Composable
fun IceLureNotesTheme(
    darkTheme: Boolean = false, // Force light theme for winter aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}