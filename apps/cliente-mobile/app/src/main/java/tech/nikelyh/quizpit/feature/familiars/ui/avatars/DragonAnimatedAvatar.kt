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
fun DragonAnimatedAvatar(
    modifier: Modifier = Modifier,
    isAnimationReady: Boolean = true,
    isPressed: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    BaseAvatarEngine(
        avatar = FamiliarAvatar.Dragon,
        modifier = modifier,
        isAnimationReady = isAnimationReady,
        isPressed = isPressed,
        onAvatarClick = onClick
    ) { state ->

        // Animaciones específicas (Siguen pudiendo recomponer pero aisladas en este nivel)
        val tailRotation by state.infiniteTransition.animateFloat(
            initialValue = -8f,
            targetValue = 12f,
            animationSpec = infiniteRepeatable(
                animation = tween(2000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "TailSwish"
        )

        val wingRotation by state.infiniteTransition.animateFloat(
            initialValue = -20f,
            targetValue = 15f,
            animationSpec = infiniteRepeatable(
                animation = tween(1200, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            ),
            label = "WingFlap"
        )

        Box(modifier = Modifier.fillMaxSize()) {
            // Lectura Diferida en cada capa
            Image(
                painter = painterResource(id = R.drawable.ic_dragon_tail),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        transformOrigin = TransformOrigin(0.7f, 0.7f)
                        rotationZ = if (isAnimationReady) tailRotation else 0f
                        translationX = state.currentTiltX * 0.4f
                        translationY = state.currentBreathing * 0.5f
                    }
            )

            Image(
                painter = painterResource(id = R.drawable.ic_dragon_wing_back),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        transformOrigin = TransformOrigin(0.55f, 0.5f)
                        rotationZ = (if (isAnimationReady) wingRotation else 0f) * 0.7f
                        translationX = state.currentTiltX * 0.2f
                        translationY = state.currentBreathing
                    }
            )

            Image(
                painter = painterResource(id = R.drawable.ic_dragon_body),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationX = state.currentTiltX
                        translationY = state.currentBreathing + state.currentTiltY
                    }
            )

            Image(
                painter = painterResource(id = R.drawable.ic_dragon_wing_front),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        transformOrigin = TransformOrigin(0.5f, 0.45f)
                        rotationZ = if (isAnimationReady) wingRotation else 0f
                        translationX = state.currentTiltX * 0.3f
                        translationY = state.currentBreathing
                    }
            )
        }
    }
}
