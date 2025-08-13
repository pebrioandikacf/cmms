package com.ptpn.cmms.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// Pastikan CMMS_* dideklarasikan di Color.kt dalam package yang sama.
// Contoh: CMMS_Primary, CMMS_OnPrimary, CMMS_Secondary, CMMS_OnSecondary, CMMS_Background, CMMS_Surface, CMMS_SurfaceVariant, CMMS_OnSurface, CMMS_Error

private val LightColorScheme = lightColorScheme(
    primary = CMMS_Primary,
    onPrimary = CMMS_OnPrimary,
    secondary = CMMS_Secondary,
    onSecondary = CMMS_OnSecondary,
    tertiary = androidx.compose.ui.graphics.Color(0xFFB0BEC5),
    background = CMMS_Background,
    onBackground = CMMS_OnSurface,
    surface = CMMS_Surface,
    onSurface = CMMS_OnSurface,
    surfaceVariant = CMMS_SurfaceVariant,
    error = CMMS_Error
)

private val DarkColorScheme = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF90CAF9),
    onPrimary = androidx.compose.ui.graphics.Color.Black,
    secondary = CMMS_Secondary,
    onSecondary = CMMS_OnSecondary,
    tertiary = androidx.compose.ui.graphics.Color(0xFFB0BEC5),
    background = androidx.compose.ui.graphics.Color(0xFF121212),
    onBackground = androidx.compose.ui.graphics.Color.White,
    surface = androidx.compose.ui.graphics.Color(0xFF1E1E1E),
    onSurface = androidx.compose.ui.graphics.Color.White,
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFF2A2A2A),
    error = CMMS_Error
)

@Composable
fun CmmsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val ctx = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(ctx) else dynamicLightColorScheme(ctx)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        // shapes = Shapes // <= jangan pakai jika kamu belum mendeklarasikan Shapes val
        content = content
    )
}
