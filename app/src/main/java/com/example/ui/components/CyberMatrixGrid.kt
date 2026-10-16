package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberGreen

@Composable
fun CyberMatrixGrid(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "MatrixScanAnim")

    val scanLineY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanLine"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Subtle dark cyber background
        drawRect(color = CyberBackground)

        // Cyber Grid Lines (Vertical & Horizontal)
        val gridSpacing = 40f
        val gridColor = Color(0xFF09162B)

        var x = 0f
        while (x <= width) {
            drawLine(
                color = gridColor,
                start = Offset(x, 0f),
                end = Offset(x, height),
                strokeWidth = 1f
            )
            x += gridSpacing
        }

        var y = 0f
        while (y <= height) {
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
            y += gridSpacing
        }

        // Animated Cyber Laser Scan Line
        val currentScanY = height * scanLineY
        drawLine(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    CyberCyan.copy(alpha = 0.6f),
                    CyberGreen.copy(alpha = 0.8f),
                    CyberCyan.copy(alpha = 0.6f),
                    Color.Transparent
                )
            ),
            start = Offset(0f, currentScanY),
            end = Offset(width, currentScanY),
            strokeWidth = 2.5f
        )

        // Trailing scan glow
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    CyberCyan.copy(alpha = 0.08f),
                    Color.Transparent
                ),
                startY = (currentScanY - 120f).coerceAtLeast(0f),
                endY = currentScanY
            ),
            topLeft = Offset(0f, (currentScanY - 120f).coerceAtLeast(0f)),
            size = androidx.compose.ui.geometry.Size(width, 120f)
        )
    }
}
