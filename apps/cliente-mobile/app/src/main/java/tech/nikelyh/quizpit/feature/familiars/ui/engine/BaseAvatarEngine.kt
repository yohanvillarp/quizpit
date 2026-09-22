package tech.nikelyh.quizpit.feature.familiars.ui.engine

import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import tech.nikelyh.quizpit.core.sensor.LocalAvatarTilt
import tech.nikelyh.quizpit.core.sensor.TiltState
import tech.nikelyh.quizpit.feature.familiars.core.FamiliarAvatar

/**
 * Motor central optimizado para alto rendimiento (60fps).
 * Utiliza "Lectura Diferida" para evitar recomposiciones por sensores.
 */
@Composable
fun BaseAvatarEngine(
    avatar: FamiliarAvatar,
    modifier: Modifier = Modifier,
    isAnimationReady: Boolean = true,
    isPressed: Boolean = false,
    onAvatarClick: (() -> Unit)? = null,
    content: @Composable (BaseAvatarState) -> Unit
) {
    var internalIsPressed by remember { mutableStateOf(false) }
    val finalIsPressed = if (onAvatarClick != null) internalIsPressed else isPressed

    // 1. Obtenemos la referencia estable del Tilt (No dispara recomposición al cambiar x/y)
    val globalTilt = LocalAvatarTilt.current

    // 2. Animación de Respiración (Diferida mediante State)
    val infiniteTransition = rememberInfiniteTransition(label = "BaseBreathing")
    val breathingOffset = infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Breathing"
    )

    // 3. Efecto de Escala (Este sí puede recomponer el Box contenedor, lo cual es aceptable por ser esporádico)
    val scale by animateFloatAsState(
        targetValue = if (finalIsPressed) 0.9f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow),
        label = "PressScale"
    )

    // Preparamos el estado para los hijos.
    val state = remember(globalTilt, breathingOffset, isAnimationReady, infiniteTransition) {
        BaseAvatarState(
            tilt = globalTilt,
            breathingOffsetState = breathingOffset,
            isAnimationReady = isAnimationReady,
            infiniteTransition = infiniteTransition
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .scale(scale)
            .then(
                if (onAvatarClick != null) {
                    Modifier.clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            internalIsPressed = !internalIsPressed
                            onAvatarClick.invoke()
                        }
                    )
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        content(state)
    }
}

/**
 * Estado que permite la lectura diferida de valores de animación y sensores.
 */
@Stable
data class BaseAvatarState(
    val tilt: TiltState,
    val breathingOffsetState: State<Float>,
    val isAnimationReady: Boolean,
    val infiniteTransition: InfiniteTransition
) {
    // Lectura diferida de Tilt X
    val currentTiltX: Float get() = if (isAnimationReady) tilt.x else 0f

    // Lectura diferida de Tilt Y
    val currentTiltY: Float get() = if (isAnimationReady) tilt.y else 0f

    // Lectura diferida de Respiración
    val currentBreathing: Float get() = if (isAnimationReady) breathingOffsetState.value else 0f
}
