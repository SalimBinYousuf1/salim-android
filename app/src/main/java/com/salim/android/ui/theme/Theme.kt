package com.salim.android.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val WhatsAppGreen = Color(0xFF128C7E)
val WhatsAppGreenDark = Color(0xFF075E54)
val WhatsAppGreenLight = Color(0xFF25D366)
val WhatsAppTeal = Color(0xFF34B7F1)
val BubbleOut = Color(0xFFDCF8C6)
val BubbleIn = Color(0xFFFFFFFF)
val BubbleOutDark = Color(0xFF056162)
val BubbleInDark = Color(0xFF1F2C34)
val BackgroundDark = Color(0xFF0D1418)
val SurfaceDark = Color(0xFF1F2C34)

private val LightColors = lightColorScheme(
    primary = WhatsAppGreen,
    onPrimary = Color.White,
    primaryContainer = WhatsAppGreenLight,
    secondary = WhatsAppGreenDark,
    background = Color(0xFFF0F0F0),
    surface = Color.White,
    surfaceVariant = Color(0xFFECECEC),
)

private val DarkColors = darkColorScheme(
    primary = WhatsAppGreenLight,
    onPrimary = Color.Black,
    secondary = WhatsAppTeal,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = Color(0xFF2A3942),
)

@Composable
fun SalimTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
