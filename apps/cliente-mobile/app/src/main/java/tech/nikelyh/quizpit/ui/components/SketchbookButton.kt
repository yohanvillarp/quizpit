package tech.nikelyh.quizpit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring

@Composable
fun SketchbookButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary,
    borderColor: Color = MaterialTheme.colorScheme.onBackground,
    shadowColor: Color = MaterialTheme.colorScheme.onBackground,
    borderWidth: Dp = 3.dp,
    shadowOffset: Dp = 6.dp,
    cornerRadius: Dp = 12.dp,
    content: @Composable RowScope.() -> Unit
) {
    val currentBgColor = if (enabled) backgroundColor else Color.LightGray
    val currentBorderColor = if (enabled) borderColor else Color.Gray
    val currentShadowColor = if (enabled) shadowColor else Color.Gray
    val currentContentColor = if (enabled) contentColor else Color.DarkGray
    
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Si está deshabilitado, la sombra es baja (2dp). Si está habilitado pero presionado, baja a 0dp. Si no, tamaño normal.
    val targetShadowOffset = if (!enabled) 2.dp else if (isPressed) 0.dp else shadowOffset
    
    val activeShadowOffset by animateDpAsState(
        targetValue = targetShadowOffset,
        animationSpec = spring(stiffness = Spring.StiffnessHigh),
        label = "ButtonShadow"
    )
    
    // Empujar el botón hacia abajo (offset de UI) para que se junte con la sombra cuando se presiona
    val surfaceOffset = shadowOffset - activeShadowOffset

    Box(
        modifier = modifier.padding(bottom = shadowOffset, end = shadowOffset)
    ) {
        // Sombra sólida fija (no se mueve)
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowOffset, y = shadowOffset)
                .background(currentShadowColor, RoundedCornerShape(cornerRadius))
        )
        
        // Superficie del botón (se mueve hacia la sombra al ser presionada)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = surfaceOffset, y = surfaceOffset)
                .background(Color(0xFFFAF9F5), RoundedCornerShape(cornerRadius)) // Fondo de papel sólido
                .clip(RoundedCornerShape(cornerRadius))
                .sketchbookColoring(currentBgColor)
                .border(borderWidth, currentBorderColor, RoundedCornerShape(cornerRadius))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null, // Desactivar el ripple normal de Android
                    enabled = enabled,
                    onClick = onClick
                )
                .padding(vertical = 16.dp, horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
        ) {
            androidx.compose.runtime.CompositionLocalProvider(
                androidx.compose.material3.LocalContentColor provides currentContentColor
            ) {
                content()
            }
        }
    }
}
