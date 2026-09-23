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
fun BatAnimatedAvatar(
    modifier: Modifier = Modifier,
    isAnimationReady: Boolean = true,
    isPressed: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    BaseAvatarEngine(
        avatar = FamiliarAvatar.Bat,
        modifier = modifier,
        isAnimationReady = isAnimationReady,
        isPressed = isPressed,
        onAvatarClick = onClick
    ) { state ->

        val wingScaleY by state.infiniteTransition.animateFloat(
            initialValue = 0.25f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            ),
            label = "WingFlapScale"
        )

        Box(
            modifier = Modifier.fillMaxSize().graphicsLayer {
                translationX = state.currentTiltX
                translationY = state.currentBreathing + state.currentTiltY
            }
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_bat_wings_back),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        transformOrigin = TransformOrigin(0.5f, 0.45f)
                        scaleY = if (isAnimationReady) wingScaleY else 1f
                        alpha = 0.8f
                    }
            )

            Image(
                painter = painterResource(id = R.drawable.ic_bat_wings_front),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        transformOrigin = TransformOrigin(0.5f, 0.45f)
                        scaleY = if (isAnimationReady) wingScaleY * 0.85f else 1f
                    }
            )

            Image(
                painter = painterResource(id = R.drawable.ic_bat_body),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
