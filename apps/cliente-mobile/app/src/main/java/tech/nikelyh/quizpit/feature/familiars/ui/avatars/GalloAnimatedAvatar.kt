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
fun GalloAnimatedAvatar(
    modifier: Modifier = Modifier,
    isAnimationReady: Boolean = true,
    isPressed: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    BaseAvatarEngine(
        avatar = FamiliarAvatar.Gallo,
        modifier = modifier,
        isAnimationReady = isAnimationReady,
        isPressed = isPressed,
        onAvatarClick = onClick
    ) { state ->

        val tailRotation by state.infiniteTransition.animateFloat(
            initialValue = -5f,
            targetValue = 15f,
            animationSpec = infiniteRepeatable(
                animation = tween(1250, easing = LinearOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "TailSwish"
        )

        val wingRotation by state.infiniteTransition.animateFloat(
            initialValue = -10f,
            targetValue = 20f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "WingFlap"
        )

        val crestRotation by state.infiniteTransition.animateFloat(
            initialValue = -5f,
            targetValue = 5f,
            animationSpec = infiniteRepeatable(
                animation = tween(750, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "CrestBounce"
        )

        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.ic_gallo_tail),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        transformOrigin = TransformOrigin(0.6f, 0.5f)
                        rotationZ = if (isAnimationReady) tailRotation else 0f
                        translationX = state.currentTiltX * 0.3f
                    }
            )

            Image(
                painter = painterResource(id = R.drawable.ic_gallo_body),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationX = state.currentTiltX
                        translationY = state.currentBreathing + state.currentTiltY
                    }
            )

            Image(
                painter = painterResource(id = R.drawable.ic_gallo_wing),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        transformOrigin = TransformOrigin(0.35f, 0.5f)
                        rotationZ = if (isAnimationReady) wingRotation else 0f
                        translationX = state.currentTiltX * 0.5f
                    }
            )

            Image(
                painter = painterResource(id = R.drawable.ic_gallo_crest),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        transformOrigin = TransformOrigin(0.45f, 0.2f)
                        rotationZ = if (isAnimationReady) crestRotation else 0f
                        translationX = state.currentTiltX
                        translationY = state.currentBreathing + state.currentTiltY
                    }
            )
        }
    }
}
