package tech.nikelyh.quizpit.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize

@Composable
fun SketchbookStickerButton(
    icon: Painter,
    text: String,
    rotation: Float,
    backgroundColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onDragStateChanged: (Boolean) -> Unit = {},
    dragBoundsX: ClosedFloatingPointRange<Float>? = null,
    dragBoundsY: ClosedFloatingPointRange<Float>? = null
) {
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    var isDragging by remember { mutableStateOf(false) }
    var currentRotation by remember { mutableStateOf(rotation) }
    val coroutineScope = rememberCoroutineScope()
    
    val animatedScale by animateFloatAsState(targetValue = if (isDragging) 1.15f else 1f, label = "scale")

    Column(
        modifier = modifier
            .zIndex(if (isDragging) 100f else 10f)
            .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
            .graphicsLayer { 
                scaleX = animatedScale
                scaleY = animatedScale
                rotationZ = if (isDragging) 0f else currentRotation 
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { 
                        isDragging = true 
                        onDragStateChanged(true)
                    },
                    onDragEnd = { 
                        isDragging = false
                        onDragStateChanged(false)
                        currentRotation = Random.nextFloat() * 30f - 15f
                    },
                    onDragCancel = { 
                        isDragging = false 
                        onDragStateChanged(false)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        coroutineScope.launch {
                            var newX = offsetX.value + dragAmount.x
                            var newY = offsetY.value + dragAmount.y
                            
                            if (dragBoundsX != null) newX = newX.coerceIn(dragBoundsX)
                            if (dragBoundsY != null) newY = newY.coerceIn(dragBoundsY)
                            
                            offsetX.snapTo(newX)
                            offsetY.snapTo(newY)
                        }
                    }
                )
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Cuadrado principal (Pegatina grande con el ícono)
        Box(
            modifier = Modifier
                .size(72.dp)
                .graphicsLayer { rotationZ = if (isDragging) 0f else currentRotation } // Se endereza al levantarlo
                .background(backgroundColor, RoundedCornerShape(8.dp))
                .border(2.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = icon,
                contentDescription = null, 
                tint = contentColor,
                modifier = Modifier.size(40.dp) 
            )
        }
        
        // Trozo de papel "cortado" (Pegatina pequeña para el texto)
        Box(
            modifier = Modifier
                .offset(y = (-6).dp) 
                .graphicsLayer { rotationZ = if (isDragging) 0f else -currentRotation * 0.5f }
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
