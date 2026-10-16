package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.service.JarvisVoiceState
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisGold
import com.example.ui.theme.JarvisGreen
import kotlin.math.sin

@Composable
fun VoiceWaveVisualizer(
    voiceState: JarvisVoiceState,
    audioRmsDb: Float,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "WaveVisualizerAnim")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )

    val activeColor = when (voiceState) {
        JarvisVoiceState.LISTENING -> JarvisGold
        JarvisVoiceState.SPEAKING -> JarvisGreen
        else -> JarvisCyan
    }

    Canvas(
        modifier = modifier
            .width(260.dp)
            .height(36.dp)
    ) {
        val barCount = 28
        val spacing = size.width / barCount
        val barWidth = spacing * 0.55f
        val centerY = size.height / 2f

        for (i in 0 until barCount) {
            val normalizedX = i.toFloat() / barCount
            val wave = (sin(normalizedX * 10f + phase) + 1f) / 2f

            val amplitude = when (voiceState) {
                JarvisVoiceState.LISTENING -> {
                    val audioBoost = (audioRmsDb * 1.8f).coerceIn(0.1f, 1.0f)
                    (wave * 0.5f + audioBoost * 0.5f).coerceIn(0.15f, 1.0f)
                }
                JarvisVoiceState.SPEAKING -> {
                    (wave * 0.7f + 0.3f).coerceIn(0.2f, 0.95f)
                }
                JarvisVoiceState.PROCESSING -> {
                    (sin(phase * 2f + i * 0.5f) * 0.4f + 0.5f).coerceIn(0.1f, 0.8f)
                }
                else -> {
                    0.1f // idle minimal pulse
                }
            }

            val barHeight = (size.height * amplitude).coerceAtLeast(4.dp.toPx())
            val topY = centerY - (barHeight / 2f)
            val leftX = i * spacing + (spacing - barWidth) / 2f

            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        activeColor.copy(alpha = 0.9f),
                        activeColor.copy(alpha = 0.4f)
                    ),
                    startY = topY,
                    endY = topY + barHeight
                ),
                topLeft = Offset(leftX, topY),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
            )
        }
    }
}
