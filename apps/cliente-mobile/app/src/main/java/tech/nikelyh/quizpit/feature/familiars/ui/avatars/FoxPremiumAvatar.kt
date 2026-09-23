package tech.nikelyh.quizpit.feature.familiars.ui.avatars

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import tech.nikelyh.quizpit.R
import tech.nikelyh.quizpit.feature.familiars.core.FamiliarAvatar
import tech.nikelyh.quizpit.feature.familiars.ui.engine.BaseAvatarEngine

@Composable
fun FoxPremiumAvatar(
    modifier: Modifier = Modifier,
    isAnimationReady: Boolean = true,
    isPressed: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    BaseAvatarEngine(
        avatar = FamiliarAvatar.Fox,
        modifier = modifier,
        isAnimationReady = isAnimationReady,
        isPressed = isPressed,
        onAvatarClick = onClick
    ) { state ->

        // Animaciones infinitas propias (estas recomponen levemente pero están optimizadas)
        val tailRotation by state.infiniteTransition.animateFloat(
            initialValue = -15f,
            targetValue = 25f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "FoxTailWag"
        )

        val eyeTranslationX by state.infiniteTransition.animateFloat(
            initialValue = -3f,
            targetValue = 3f,
            animationSpec = infiniteRepeatable(
                animation = tween(2500, easing = LinearOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "FoxEyesLook"
        )

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            // Cola: Lectura diferida del sensor
            Image(
                painter = painterResource(id = R.drawable.ic_fox_tail),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .graphicsLayer {
                        transformOrigin = TransformOrigin(0.5f, 0.75f)
                        rotationZ = if (isAnimationReady) tailRotation else -15f
                        translationX = state.currentTiltX * 0.2f
                    }
            )

            // Cabeza: Lectura diferida total
            Image(
                painter = painterResource(id = R.drawable.ic_fox_head),
                contentDescription = "Zorro Ladrón Astuto",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .graphicsLayer {
                        translationX = state.currentTiltX
                        translationY = state.currentBreathing + state.currentTiltY
                    }
            )

            // Ojos: Lectura diferida
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .graphicsLayer {
                        translationX = (if (isAnimationReady) eyeTranslationX * density else 0f) + state.currentTiltX
                        translationY = state.currentBreathing + state.currentTiltY
                    }
            ) {
                val centerX = size.width / 2
                val centerY = size.height / 2

                val leftEye = Offset(centerX - size.width * 0.18f, centerY + size.height * 0.06f)
                val rightEye = Offset(centerX + size.width * 0.18f, centerY + size.height * 0.06f)

                val eyeWidth = size.width * 0.04f
                val eyeHeight = size.height * 0.075f

                drawOval(
                    color = Color(0xFF1a1f3a),
                    topLeft = Offset(leftEye.x - eyeWidth / 2, leftEye.y - eyeHeight / 2),
                    size = androidx.compose.ui.geometry.Size(eyeWidth, eyeHeight)
                )
                drawCircle(
                    color = Color.White,
                    radius = eyeWidth * 0.25f,
                    center = Offset(leftEye.x + eyeWidth * 0.15f, leftEye.y - eyeHeight * 0.2f)
                )

                drawOval(
                    color = Color(0xFF1a1f3a),
                    topLeft = Offset(rightEye.x - eyeWidth / 2, rightEye.y - eyeHeight / 2),
                    size = androidx.compose.ui.geometry.Size(eyeWidth, eyeHeight)
                )
                drawCircle(
                    color = Color.White,
                    radius = eyeWidth * 0.25f,
                    center = Offset(rightEye.x + eyeWidth * 0.15f, rightEye.y - eyeHeight * 0.2f)
                )
            }
        }
    }
}
