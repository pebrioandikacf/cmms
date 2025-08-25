package com.ptpn.cmms.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color

// --- Colors (pakai yang sudah Anda punya) ---
private val LightColorScheme = lightColorScheme(
    primary = CMMS_Primary,
    onPrimary = CMMS_OnPrimary,
    primaryContainer = CMMS_PrimaryContainer,
    onPrimaryContainer = CMMS_OnPrimary,

    secondary = CMMS_Secondary,
    onSecondary = CMMS_OnSecondary,

    background = CMMS_Background,
    onBackground = CMMS_OnBackground,

    surface = CMMS_Surface,
    onSurface = CMMS_OnSurface,

    surfaceVariant = CMMS_SurfaceVariant,
    onSurfaceVariant = CMMS_OnSurfaceVariant,

    error = CMMS_Error,
    onError = CMMS_OnError,

)

private val DarkColorScheme = darkColorScheme(
    primary = CMMS_PrimaryContainer,
    onPrimary = CMMS_OnPrimary,
    primaryContainer = CMMS_Primary,
    onPrimaryContainer = CMMS_OnPrimary,

    secondary = CMMS_Secondary,
    onSecondary = CMMS_OnSecondary,

    background = Color(0xFF081118),
    onBackground = CMMS_OnPrimary,

    surface = Color(0xFF0D1A24),
    onSurface = CMMS_OnPrimary,

    surfaceVariant = Color(0xFF16252C),
    onSurfaceVariant = CMMS_OnPrimary,

    error = CMMS_Error,
    onError = CMMS_OnError
)

// --- Shapes: definisikan sebagai value (hindari nama yang bentrok) ---
val AppShapes = Shapes(
    // default Material3 Shapes takes small/medium/large as CornerBasedShape
    // gunakan RoundedCornerShape supaya konsisten dengan UI Anda
    small = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
    medium = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
    large = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
)

// --- Typography minimal (sesuaikan jika Anda punya font family custom) ---
val AppTypography = Typography(
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    ),
    titleSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    )
)

// --- Theme wrapper ---
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
        typography = AppTypography,
        shapes = AppShapes,
        content = content
    )
}
