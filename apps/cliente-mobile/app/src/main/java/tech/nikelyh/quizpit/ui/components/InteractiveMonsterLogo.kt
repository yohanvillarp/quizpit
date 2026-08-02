package tech.nikelyh.quizpit.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.launch
import tech.nikelyh.quizpit.R
import kotlin.math.roundToInt

@Composable
fun InteractiveMonsterLogo(
    modifier: Modifier = Modifier,
    onDragStateChanged: (Boolean) -> Unit = {},
    dragBoundsX: ClosedFloatingPointRange<Float>? = null,
    dragBoundsY: ClosedFloatingPointRange<Float>? = null
) {
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    
    var isInteracting by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Image(
        painter = painterResource(id = R.drawable.logo_quizpit),
        contentDescription = "QuizPit Logo Interactivo",
        modifier = modifier
            .size(200.dp)
            .zIndex(if (isInteracting) 50f else 1f)
            .offset { IntOffset(offsetX.value.roundToInt(), offsetY.value.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { 
                        isInteracting = true 
                        onDragStateChanged(true)
                    },
                    onDragEnd = {
                        isInteracting = false
                        onDragStateChanged(false)
                    },
                    onDragCancel = {
                        isInteracting = false
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
    )
}
