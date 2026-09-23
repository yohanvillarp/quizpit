package tech.nikelyh.quizpit.feature.familiars.ui.avatars

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import tech.nikelyh.quizpit.R
import tech.nikelyh.quizpit.feature.familiars.core.FamiliarAvatar
import tech.nikelyh.quizpit.feature.familiars.ui.engine.BaseAvatarEngine

@Composable
fun ChameleonAnimatedAvatar(
    modifier: Modifier = Modifier,
    isAnimationReady: Boolean = true,
    isPressed: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    BaseAvatarEngine(
        avatar = FamiliarAvatar.Chameleon,
        modifier = modifier,
        isAnimationReady = isAnimationReady,
        isPressed = isPressed,
        onAvatarClick = onClick
    ) { state ->

        val tailScale by state.infiniteTransition.animateFloat(
            initialValue = 0.9f,
            targetValue = 1.1f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "TailScale"
        )

        val tongueTranslateX by state.infiniteTransition.animateFloat(
            initialValue = -10f,
            targetValue = 20f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 2000
                    -10f at 0
                    -10f at 1600
                    20f at 1700 with FastOutLinearInEasing
                    20f at 1800
                    -10f at 2000 with LinearOutSlowInEasing
                },
                repeatMode = RepeatMode.Restart
            ),
            label = "TongueDart"
        )

        val eyeRotation by state.infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(4000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "EyeSpin"
        )

        Box(
            modifier = Modifier.fillMaxSize().graphicsLayer {
                translationX = state.currentTiltX
                translationY = state.currentBreathing + state.currentTiltY
            }
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_chameleon_tail),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        transformOrigin = TransformOrigin(0.25f, 0.7f)
                        scaleX = if (isAnimationReady) tailScale else 1f
                        scaleY = if (isAnimationReady) tailScale else 1f
                    }
            )

            Image(
                painter = painterResource(id = R.drawable.ic_chameleon_body),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )

            Image(
                painter = painterResource(id = R.drawable.ic_chameleon_tongue),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationX = if (isAnimationReady) tongueTranslateX else -10f
                    }
            )

            Image(
                painter = painterResource(id = R.drawable.ic_chameleon_eye),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        transformOrigin = TransformOrigin(0.725f, 0.475f)
                        rotationZ = if (isAnimationReady) eyeRotation else 0f
                    }
            )
        }
    }
}
