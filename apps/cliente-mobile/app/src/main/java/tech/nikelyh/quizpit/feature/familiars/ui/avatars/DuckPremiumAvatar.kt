package tech.nikelyh.quizpit.feature.familiars.ui.avatars

import android.graphics.RenderEffect
import android.os.Build
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import tech.nikelyh.quizpit.R
import tech.nikelyh.quizpit.feature.familiars.core.FamiliarAvatar
import tech.nikelyh.quizpit.feature.familiars.ui.engine.BaseAvatarEngine

@Composable
fun DuckPremiumAvatar(
    modifier: Modifier = Modifier,
    isAnimationReady: Boolean = true,
    isPressed: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    BaseAvatarEngine(
        avatar = FamiliarAvatar.Duck,
        modifier = modifier,
        isAnimationReady = isAnimationReady,
        isPressed = isPressed,
        onAvatarClick = onClick
    ) { state ->

        val time by state.infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 3600f,
            animationSpec = infiniteRepeatable(
                animation = tween(3600000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "DuckShaderTime"
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    // Lectura Diferida del Sensor y Respiración
                    translationX = state.currentTiltX
                    translationY = state.currentBreathing + state.currentTiltY

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val shader = tech.nikelyh.quizpit.feature.familiars.ui.shaders.ShaderCache.getWaterRippleShader()
                        shader.setFloatUniform("time", time)
                        shader.setFloatUniform("resolution", size.width, size.height)
                        renderEffect = RenderEffect
                            .createRuntimeShaderEffect(shader, "contents")
                            .asComposeRenderEffect()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_duck),
                contentDescription = "Sir Quack Mítico",
                modifier = Modifier.fillMaxSize().padding(8.dp)
            )
        }
    }
}
