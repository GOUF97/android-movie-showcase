package com.mandiri.movieapp.presentation.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = MandiriGold,
    onPrimary = MandiriDarkBg,
    primaryContainer = MandiriBlue,
    onPrimaryContainer = TextPrimary,
    secondary = MandiriBlue,
    onSecondary = TextPrimary,
    background = MandiriDarkBg,
    onBackground = TextPrimary,
    surface = MandiriSurface,
    onSurface = TextPrimary,
    surfaceVariant = MandiriSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    error = ErrorRed,
    onError = TextPrimary
)

@Composable
fun MandiriMovieTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = MandiriDarkBg.toArgb()
                window.navigationBarColor = MandiriDarkBg.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
