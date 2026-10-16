package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.JarvisVoiceState
import com.example.ui.theme.JarvisCyan
import com.example.ui.theme.JarvisCyanLight
import com.example.ui.theme.JarvisGold
import com.example.ui.theme.JarvisGreen
import com.example.ui.theme.JarvisRed
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ArcReactorView(
    voiceState: JarvisVoiceState,
    audioRmsDb: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ArcReactorTransitions")

    // Slow clockwise rotation for outer ring
    val outerAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "outerRotate"
    )

    // Counter-clockwise rotation for inner ring
    val innerAngle by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(7000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "innerRotate"
    )

    // Breathing pulse for core
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "corePulse"
    )

    val stateColor = when (voiceState) {
        JarvisVoiceState.STANDBY -> JarvisCyan
        JarvisVoiceState.LISTENING -> JarvisGold
        JarvisVoiceState.PROCESSING -> JarvisCyanLight
        JarvisVoiceState.SPEAKING -> JarvisGreen
        JarvisVoiceState.ERROR -> JarvisRed
    }

    val stateLabel = when (voiceState) {
        JarvisVoiceState.STANDBY -> "TAP TO ENGAGE"
        JarvisVoiceState.LISTENING -> "LISTENING..."
        JarvisVoiceState.PROCESSING -> "ANALYZING..."
        JarvisVoiceState.SPEAKING -> "TRANSMITTING..."
        JarvisVoiceState.ERROR -> "SYSTEM ALERT"
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(220.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = false, radius = 110.dp),
                    onClick = onClick
                )
                .testTag("arc_reactor_button")
        ) {
            Canvas(modifier = Modifier.size(200.dp)) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val baseRadius = size.width / 2f

                // Outer ambient glow circle
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            stateColor.copy(alpha = 0.35f * pulseScale),
                            stateColor.copy(alpha = 0.08f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = baseRadius * 1.1f
                    ),
                    radius = baseRadius,
                    center = center
                )

                // Outer segmented ring with rotation
                rotate(outerAngle, pivot = center) {
                    val segments = 12
                    for (i in 0 until segments) {
                        val angleDeg = (i * 360f / segments)
                        val angleRad = Math.toRadians(angleDeg.toDouble())
                        val p1 = Offset(
                            center.x + (baseRadius * 0.90f * cos(angleRad)).toFloat(),
                            center.y + (baseRadius * 0.90f * sin(angleRad)).toFloat()
                        )
                        val p2 = Offset(
                            center.x + (baseRadius * 0.98f * cos(angleRad)).toFloat(),
                            center.y + (baseRadius * 0.98f * sin(angleRad)).toFloat()
                        )
                        drawLine(
                            color = JarvisGold.copy(alpha = 0.8f),
                            start = p1,
                            end = p2,
                            strokeWidth = 3.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }

                    // Outer thin track
                    drawCircle(
                        color = JarvisCyan.copy(alpha = 0.5f),
                        radius = baseRadius * 0.88f,
                        center = center,
                        style = Stroke(
                            width = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 10f), 0f)
                        )
                    )
                }

                // Middle reverse counter-rotating segmented arcs
                rotate(innerAngle, pivot = center) {
                    drawArc(
                        color = stateColor.copy(alpha = 0.85f),
                        startAngle = 0f,
                        sweepAngle = 75f,
                        useCenter = false,
                        topLeft = Offset(center.x - baseRadius * 0.72f, center.y - baseRadius * 0.72f),
                        size = androidx.compose.ui.geometry.Size(baseRadius * 1.44f, baseRadius * 1.44f),
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = stateColor.copy(alpha = 0.85f),
                        startAngle = 120f,
                        sweepAngle = 75f,
                        useCenter = false,
                        topLeft = Offset(center.x - baseRadius * 0.72f, center.y - baseRadius * 0.72f),
                        size = androidx.compose.ui.geometry.Size(baseRadius * 1.44f, baseRadius * 1.44f),
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )
                    drawArc(
                        color = stateColor.copy(alpha = 0.85f),
                        startAngle = 240f,
                        sweepAngle = 75f,
                        useCenter = false,
                        topLeft = Offset(center.x - baseRadius * 0.72f, center.y - baseRadius * 0.72f),
                        size = androidx.compose.ui.geometry.Size(baseRadius * 1.44f, baseRadius * 1.44f),
                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Inner core energy ring - reacts to audio or breathing
                val dynamicCoreScale = if (voiceState == JarvisVoiceState.LISTENING) {
                    1f + (audioRmsDb * 0.4f)
                } else if (voiceState == JarvisVoiceState.SPEAKING) {
                    1.1f * pulseScale
                } else {
                    pulseScale
                }

                val coreRadius = baseRadius * 0.48f * dynamicCoreScale

                // Solid inner reactor core
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White,
                            stateColor,
                            stateColor.copy(alpha = 0.4f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = coreRadius
                    ),
                    radius = coreRadius,
                    center = center
                )

                // High-tech center emblem circle
                drawCircle(
                    color = stateColor,
                    radius = baseRadius * 0.22f,
                    center = center,
                    style = Stroke(width = 2.5.dp.toPx())
                )

                // Inner triangular reactor core lines
                val triRadius = baseRadius * 0.16f
                for (j in 0..2) {
                    val a1 = Math.toRadians((j * 120.0) - 90.0)
                    val a2 = Math.toRadians(((j + 1) * 120.0) - 90.0)
                    drawLine(
                        color = Color.White.copy(alpha = 0.9f),
                        start = Offset(
                            center.x + (triRadius * cos(a1)).toFloat(),
                            center.y + (triRadius * sin(a1)).toFloat()
                        ),
                        end = Offset(
                            center.x + (triRadius * cos(a2)).toFloat(),
                            center.y + (triRadius * sin(a2)).toFloat()
                        ),
                        strokeWidth = 2.dp.toPx()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stateLabel,
            color = stateColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}
