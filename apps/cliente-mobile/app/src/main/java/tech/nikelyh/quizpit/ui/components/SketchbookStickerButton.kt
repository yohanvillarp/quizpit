package tech.nikelyh.quizpit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SketchbookStickerButton(
    icon: Painter,
    text: String,
    rotation: Float,
    backgroundColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null, // Desactivamos el ripple cuadrado por defecto para que no rompa la ilusión del sticker
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Cuadrado principal (Pegatina grande con el ícono)
        Box(
            modifier = Modifier
                .size(72.dp)
                .graphicsLayer { rotationZ = rotation }
                .background(backgroundColor, RoundedCornerShape(8.dp))
                .border(2.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = icon,
                contentDescription = null, // El texto abajo sirve de descripción
                tint = contentColor,
                modifier = Modifier.size(40.dp) // Ícono más grande y visible
            )
        }
        
        // Trozo de papel "cortado" (Pegatina pequeña para el texto)
        Box(
            modifier = Modifier
                .offset(y = (-6).dp) // Lo subimos un poco para que se superponga al cuadrado principal
                .graphicsLayer { rotationZ = -rotation * 0.5f } // Rotación contraria sutil
                .background(backgroundColor, RoundedCornerShape(4.dp))
                .border(2.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(4.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                color = contentColor
            )
        }
    }
}
