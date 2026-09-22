package tech.nikelyh.quizpit.feature.familiars.ui.avatars

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import tech.nikelyh.quizpit.feature.familiars.core.FamiliarAvatar
import tech.nikelyh.quizpit.feature.familiars.ui.getFamiliarDrawableId
import tech.nikelyh.quizpit.feature.familiars.ui.engine.BaseAvatarEngine

@Composable
fun AnimatedFamiliarIcon(
    id: String,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    isLocked: Boolean = false,
    isAnimationReady: Boolean = true,
    isPressed: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val avatar = remember(id) { FamiliarAvatar.fromId(id, getFamiliarDrawableId(id)) }

    Box(
        modifier = modifier.graphicsLayer {
            if (isLocked) {
                colorFilter = ColorFilter.tint(Color(0xFF1A1F3A).copy(alpha = 0.6f), BlendMode.SrcIn)
            }
        },
        contentAlignment = Alignment.Center
    ) {
        val squareModifier = Modifier
            .fillMaxSize()
            .aspectRatio(1f)

        when (avatar) {
            is FamiliarAvatar.Fox -> FoxPremiumAvatar(squareModifier, isAnimationReady, isPressed, onClick)
            is FamiliarAvatar.Medusa -> JellyfishPremiumAvatar(squareModifier, isAnimationReady, isPressed, onClick)
            is FamiliarAvatar.Dragon -> DragonAnimatedAvatar(squareModifier, isAnimationReady, isPressed, onClick)
            is FamiliarAvatar.Duck -> DuckPremiumAvatar(squareModifier, isAnimationReady, isPressed, onClick)
            is FamiliarAvatar.Bat -> BatAnimatedAvatar(squareModifier, isAnimationReady, isPressed, onClick)
            is FamiliarAvatar.Gallo -> GalloAnimatedAvatar(squareModifier, isAnimationReady, isPressed, onClick)
            is FamiliarAvatar.Peacock -> PeacockAnimatedAvatar(squareModifier, isAnimationReady, isPressed, onClick)
            is FamiliarAvatar.Chameleon -> ChameleonAnimatedAvatar(squareModifier, isAnimationReady, isPressed, onClick)
            else -> {
                BaseAvatarEngine(
                    avatar = avatar,
                    modifier = squareModifier,
                    isAnimationReady = isAnimationReady,
                    isPressed = isPressed,
                    onAvatarClick = onClick
                ) { state ->
                    Image(
                        painter = painterResource(id = avatar.headRes),
                        contentDescription = contentDescription,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                // LECTURA DIFERIDA: Leemos los valores del estado dentro de la lambda
                                translationX = state.currentTiltX
                                translationY = state.currentBreathing + state.currentTiltY
                            }
                    )
                }
            }
        }
    }
}
