package tech.nikelyh.quizpit.feature.familiars.ui.avatars

import android.graphics.RenderEffect
import android.os.Build
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import tech.nikelyh.quizpit.R
import tech.nikelyh.quizpit.feature.familiars.core.FamiliarAvatar
import tech.nikelyh.quizpit.feature.familiars.ui.engine.BaseAvatarEngine
import kotlin.math.abs
import kotlin.math.sin

@Composable
fun JellyfishPremiumAvatar(
    modifier: Modifier = Modifier,
    isAnimationReady: Boolean = true,
    isPressed: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    BaseAvatarEngine(
        avatar = FamiliarAvatar.Medusa,
        modifier = modifier,
        isAnimationReady = isAnimationReady,
        isPressed = isPressed,
        onAvatarClick = onClick
    ) { state ->

        // 1. Animaciones específicas (Diferidas)
        val pulseProgress by state.infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "JellyfishPulse"
        )

        val time by state.infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 3600f,
            animationSpec = infiniteRepeatable(
                animation = tween(3600000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "JellyfishShaderTime"
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds()
                .graphicsLayer {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val shader = tech.nikelyh.quizpit.feature.familiars.ui.shaders.ShaderCache.getBioluminescenceShader()
                        shader.setFloatUniform("time", time)
                        shader.setFloatUniform("resolution", size.width, size.height)
                        renderEffect = RenderEffect
                            .createRuntimeShaderEffect(shader, "contents")
                            .asComposeRenderEffect()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            // Tentáculos: Lectura diferida total
            Canvas(modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    val pulseFactor = if (pulseProgress < 0.3f) {
                        FastOutSlowInEasing.transform(pulseProgress / 0.3f)
                    } else {
                        1f - LinearOutSlowInEasing.transform((pulseProgress - 0.3f) / 0.7f)
                    }
                    translationX = state.currentTiltX * 0.8f
                    translationY = (pulseFactor * 10f) + (state.currentTiltY * 0.2f)
                }
            ) {
                val pulseFactor = if (pulseProgress < 0.3f) {
                    FastOutSlowInEasing.transform(pulseProgress / 0.3f)
                } else {
                    1f - LinearOutSlowInEasing.transform((pulseProgress - 0.3f) / 0.7f)
                }

                val centerX = size.width / 2
                val baseTopY = size.height * 0.61f

                for (i in 0..4) {
                    val relativeIndex = i - 2f
                    val offsetX = relativeIndex * (size.width * 0.11f)
                    val curveOffset = abs(relativeIndex) * (size.height * 0.015f)
                    val topY = baseTopY - curveOffset
                    val length = size.width * 0.42f + (pulseFactor * 30f)

                    val path = androidx.compose.ui.graphics.Path()
                    path.moveTo(centerX + offsetX, topY)

                    val segments = 15
                    for (segment in 1..segments) {
                        val progress = segment / segments.toFloat()
                        val currentY = topY + progress * length
                        val swayAmplitude = (25f * progress) * (1f - pulseFactor * 0.5f)
                        val sway = sin(segment * 0.3f + time * 4f + i) * swayAmplitude
                        val dragX = -state.currentTiltX * progress * 3f

                        val currentX = centerX + offsetX + sway + dragX
                        path.lineTo(currentX, currentY)
                    }

                    drawPath(
                        path = path,
                        color = Color(0xFF1a1f3a),
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = 5f - (abs(relativeIndex) * 0.5f),
                            cap = StrokeCap.Round
                        )
                    )
                }
            }

            // Cabeza: Lectura diferida
            Image(
                painter = painterResource(id = R.drawable.ic_medusa_head),
                contentDescription = "Medusa Astral Mítica",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .graphicsLayer {
                        val pulseFactor = if (pulseProgress < 0.3f) {
                            FastOutSlowInEasing.transform(pulseProgress / 0.3f)
                        } else {
                            1f - LinearOutSlowInEasing.transform((pulseProgress - 0.3f) / 0.7f)
                        }
                        translationX = state.currentTiltX * 0.8f
                        translationY = (pulseFactor * 10f) + (state.currentTiltY * 0.2f)
                        rotationZ = state.currentTiltX * 0.5f
                        scaleX = 1f + (pulseFactor * 0.05f)
                        scaleY = 1f - (pulseFactor * 0.1f)
                    }
            )
        }
    }
}
