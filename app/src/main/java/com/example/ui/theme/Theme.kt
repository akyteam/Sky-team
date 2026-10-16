package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun MyApplicationTheme(
    themeMode: CyberThemeMode = CyberThemeMode.ARC_CYAN,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = themeMode.toColorScheme(),
        typography = Typography,
        content = content
    )
}
