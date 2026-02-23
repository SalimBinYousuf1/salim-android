package com.salim.android.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val WhatsAppGreen = Color(0xFF128C7E)
val WhatsAppGreenDark = Color(0xFF075E54)
val WhatsAppGreenLight = Color(0xFF25D366)
val BubbleOut = Color(0xFFDCF8C6)
val BubbleIn = Color(0xFFFFFFFF)

private val LightColors = lightColorScheme(
    primary = WhatsAppGreen,
    onPrimary = Color.White,
    secondary = WhatsAppGreenDark,
    background = Color(0xFFF0F0F0),
    surface = Color.White,
    surfaceVariant = Color(0xFFECECEC),
)

@Composable
fun SalimTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = LightColors, content = content)
}
