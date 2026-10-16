package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

enum class CyberThemeMode(
    val title: String,
    val subtitle: String,
    val primaryColor: Color,
    val accentColor: Color,
    val bgColor: Color,
    val surfaceColor: Color
) {
    ARC_CYAN(
        title = "ARC CYAN",
        subtitle = "Classic Stark Cyber HUD",
        primaryColor = Color(0xFF00E5FF),
        accentColor = Color(0xFFFFB800),
        bgColor = Color(0xFF040812),
        surfaceColor = Color(0xFF081224)
    ),
    MATRIX_GREEN(
        title = "MATRIX HACKER",
        subtitle = "Phosphor Green Terminal",
        primaryColor = Color(0xFF00FF66),
        accentColor = Color(0xFF00E5FF),
        bgColor = Color(0xFF020904),
        surfaceColor = Color(0xFF05140A)
    ),
    CRIMSON_ALERT(
        title = "CRIMSON RED",
        subtitle = "Tactical Red Alert Mode",
        primaryColor = Color(0xFFFF0055),
        accentColor = Color(0xFFFF9100),
        bgColor = Color(0xFF0A0307),
        surfaceColor = Color(0xFF16060F)
    ),
    VIOLET_VOID(
        title = "VIOLET ENCRYPT",
        subtitle = "Deep Quantum Ultraviolet",
        primaryColor = Color(0xFFB300FF),
        accentColor = Color(0xFF00E5FF),
        bgColor = Color(0xFF070312),
        surfaceColor = Color(0xFF100724)
    ),
    GOLD_TITANIUM(
        title = "GOLD TITANIUM",
        subtitle = "Mark XXI Heavy Armor",
        primaryColor = Color(0xFFFFB800),
        accentColor = Color(0xFF00E5FF),
        bgColor = Color(0xFF090703),
        surfaceColor = Color(0xFF171206)
    );

    fun toColorScheme(): ColorScheme {
        return darkColorScheme(
            primary = primaryColor,
            onPrimary = Color.Black,
            primaryContainer = primaryColor.copy(alpha = 0.2f),
            onPrimaryContainer = primaryColor,

            secondary = accentColor,
            onSecondary = Color.Black,
            secondaryContainer = accentColor.copy(alpha = 0.2f),
            onSecondaryContainer = accentColor,

            tertiary = primaryColor,
            onTertiary = Color.Black,

            background = bgColor,
            onBackground = Color(0xFFE8F7FF),

            surface = surfaceColor,
            onSurface = Color(0xFFE8F7FF),
            surfaceVariant = surfaceColor.copy(alpha = 0.7f),
            onSurfaceVariant = Color(0xFF8BB7D6),

            outline = primaryColor.copy(alpha = 0.35f),
            outlineVariant = primaryColor.copy(alpha = 0.15f),

            error = Color(0xFFFF1744),
            onError = Color.White
        )
    }
}
