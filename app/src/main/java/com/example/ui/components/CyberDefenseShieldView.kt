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
import androidx.compose.ui.graphics.Path
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
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.CyberCyanGlow
import com.example.ui.theme.CyberGold
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberRed
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CyberDefenseShieldView(
    voiceState: JarvisVoiceState,
    audioRmsDb: Float,
    isFirewallActive: Boolean = true,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ShieldTransitions")

    val outerAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shieldRotate"
    )

    val innerPulse by infiniteTransition.animateFloat(
        initialValue = 0.90f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shieldPulse"
    )

    val primaryColor = when (voiceState) {
        JarvisVoiceState.STANDBY -> if (isFirewallActive) CyberGreen else CyberCyan
        JarvisVoiceState.LISTENING -> CyberGold
        JarvisVoiceState.PROCESSING -> CyberCyanGlow
        JarvisVoiceState.SPEAKING -> CyberGreen
        JarvisVoiceState.ERROR -> CyberRed
    }

    val statusBadge = when (voiceState) {
        JarvisVoiceState.STANDBY -> if (isFirewallActive) "DEFENSE MATRIX: HARDENED" else "STEALTH MODE"
        JarvisVoiceState.LISTENING -> "VOICE ENCRYPTED: RECEIVING..."
        JarvisVoiceState.PROCESSING -> "DECRYPTING COMMAND..."
        JarvisVoiceState.SPEAKING -> "TRANSMITTING PROTOCOL..."
        JarvisVoiceState.ERROR -> "SECURITY ANOMALY"
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(230.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = false, radius = 115.dp),
                    onClick = onClick
                )
                .testTag("cyber_shield_button")
        ) {
            Canvas(modifier = Modifier.size(210.dp)) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val baseRadius = size.width / 2f

                // Outer ambient radial glow
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.25f * innerPulse),
                            primaryColor.copy(alpha = 0.05f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = baseRadius * 1.15f
                    ),
                    radius = baseRadius,
                    center = center
                )

                // Rotating Outer Hexagonal Orbit Nodes
                rotate(outerAngle, pivot = center) {
                    val nodes = 6
                    val hexRadius = baseRadius * 0.92f
                    for (i in 0 until nodes) {
                        val angleRad = Math.toRadians((i * 60.0))
                        val nx = (center.x + hexRadius * cos(angleRad)).toFloat()
                        val ny = (center.y + hexRadius * sin(angleRad)).toFloat()

                        drawCircle(
                            color = primaryColor,
                            radius = 3.5.dp.toPx(),
                            center = Offset(nx, ny)
                        )

                        // Connecting dotted arc
                        val nextAngleRad = Math.toRadians(((i + 1) * 60.0))
                        val nextX = (center.x + hexRadius * cos(nextAngleRad)).toFloat()
                        val nextY = (center.y + hexRadius * sin(nextAngleRad)).toFloat()
                        drawLine(
                            color = primaryColor.copy(alpha = 0.4f),
                            start = Offset(nx, ny),
                            end = Offset(nextX, nextY),
                            strokeWidth = 1.5.dp.toPx(),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 8f), 0f)
                        )
                    }
                }

                // Cyber Security Shield Polygon Path
                val dynamicScale = if (voiceState == JarvisVoiceState.LISTENING) {
                    1f + (audioRmsDb * 0.25f)
                } else {
                    innerPulse
                }

                val shieldW = baseRadius * 0.65f * dynamicScale
                val shieldH = baseRadius * 0.80f * dynamicScale

                val shieldPath = Path().apply {
                    moveTo(center.x, center.y - shieldH * 0.85f)
                    lineTo(center.x + shieldW * 0.85f, center.y - shieldH * 0.65f)
                    lineTo(center.x + shieldW * 0.85f, center.y + shieldH * 0.1f)
                    lineTo(center.x, center.y + shieldH * 0.85f)
                    lineTo(center.x - shieldW * 0.85f, center.y + shieldH * 0.1f)
                    lineTo(center.x - shieldW * 0.85f, center.y - shieldH * 0.65f)
                    close()
                }

                // Inner shield dark filled glass
                drawPath(
                    path = shieldPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF071B30).copy(alpha = 0.85f),
                            Color(0xFF030A14).copy(alpha = 0.95f)
                        ),
                        startY = center.y - shieldH,
                        endY = center.y + shieldH
                    )
                )

                // Shield glowing outline border
                drawPath(
                    path = shieldPath,
                    color = primaryColor,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )

                // Inner Padlock / Encryption Core
                val lockW = 20.dp.toPx()
                val lockH = 16.dp.toPx()
                val lockCenterY = center.y + 4.dp.toPx()

                // Lock Shackle (arc)
                drawArc(
                    color = primaryColor,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = false,
                    topLeft = Offset(center.x - lockW * 0.6f, lockCenterY - lockH * 1.5f),
                    size = androidx.compose.ui.geometry.Size(lockW * 1.2f, lockH * 1.5f),
                    style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                )

                // Lock Body (rect)
                drawRoundRect(
                    color = primaryColor,
                    topLeft = Offset(center.x - lockW, lockCenterY - lockH * 0.3f),
                    size = androidx.compose.ui.geometry.Size(lockW * 2f, lockH * 1.4f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )

                // Keyhole in lock
                drawCircle(
                    color = Color.Black,
                    radius = 3.dp.toPx(),
                    center = Offset(center.x, lockCenterY + lockH * 0.2f)
                )
                drawLine(
                    color = Color.Black,
                    start = Offset(center.x, lockCenterY + lockH * 0.2f),
                    end = Offset(center.x, lockCenterY + lockH * 0.7f),
                    strokeWidth = 2.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = statusBadge,
            color = primaryColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.8.sp,
            fontFamily = FontFamily.Monospace
        )
    }
}
