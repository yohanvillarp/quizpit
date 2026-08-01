package tech.nikelyh.quizpit.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = HighYellowDark,
    secondary = HighPinkDark,
    background = PaperDark,
    surface = PaperDimDark,
    onPrimary = InkLight, // Texto oscuro sobre amarillo
    onSecondary = InkLight,
    onBackground = InkDark,
    onSurface = InkDark,
    error = IncorrectDark
)

private val LightColorScheme = lightColorScheme(
    primary = HighYellowLight,
    secondary = HighPinkLight,
    background = PaperLight,
    surface = PaperDimLight,
    onPrimary = InkLight,
    onSecondary = InkLight,
    onBackground = InkLight,
    onSurface = InkLight,
    error = IncorrectLight
)

@Composable
fun QuizpitTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false, // Desactivamos el color dinámico para forzar tu estética "Sketchbook"
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