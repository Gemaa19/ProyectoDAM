package com.gema.zenitapp.ui.theme

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
    onPrimary = verdeOscuro,
    primary = verdeFondo,

    background = verdeOscuro,


    secondary = verdeIconos,
    tertiary = rosaClaro,


    scrim = Color.Black.copy(alpha = 0.5f),
    surface = Color(0xFF1B2925),
    surfaceVariant = Color(0xFF243530),


    onBackground = verdeOscuro,
    onSurface = Color(0xFFE1F5F1),
    onSurfaceVariant = verdeClaro,
)

private val LightColorScheme = lightColorScheme(
    primary = verdeOscuro,

    onPrimary = Color.White,

    secondary = verdeIconos,
    tertiary = rosa,
    background = verdeFondo,
    scrim = Color.Gray,
    surface = verdeClaro,
    surfaceVariant = verdeGrisaceo,
    onBackground = colorBoton,
    onSurface = colorBotonGeneral,
    onSurfaceVariant = verdeTitulos
)

@Composable
fun ZenitAppTheme(
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