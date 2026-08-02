package tech.nikelyh.quizpit.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

/**
 * Modificador personalizado que dibuja una cuadrícula de cuaderno (Grid)
 * en el fondo de cualquier componente, perfecto para el estilo "Analog Sketchbook".
 */
fun Modifier.sketchbookBackground(
    gridColor: Color = Color(0xFFE3E2DE), // Un gris muy sutil para que parezca lápiz suave
    gridSize: Dp = 24.dp,
    strokeWidth: Float = 2f
): Modifier = this.then(
    Modifier.drawBehind {
        val sizePx = gridSize.toPx()
        val width = size.width
        val height = size.height

        // Dibujar líneas verticales
        var x = 0f
        while (x < width) {
            drawLine(
                color = gridColor,
                start = Offset(x, 0f),
                end = Offset(x, height),
                strokeWidth = strokeWidth
            )
            x += sizePx
        }

        // Dibujar líneas horizontales
        var y = 0f
        while (y < height) {
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = strokeWidth
            )
            y += sizePx
        }
    }
)

/**
 * Modificador para animar la entrada de elementos con un rebote 
 * rápido y "seco", imitando el estilo Stop-Motion o recorte de cartón.
 */

fun Modifier.sketchbookBounceIn(): Modifier = composed {
    val scale = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    }
    
    this.graphicsLayer {
        scaleX = scale.value
        scaleY = scale.value
    }
}

/**
 * Modificador para simular un coloreado a mano con lápices de colores.
 * Dibuja trazos en zigzag deterministas para dar la ilusión de rayado.
 */


fun Modifier.sketchbookColoring(color: Color): Modifier = this.then(
    Modifier.drawBehind {
        val rnd = kotlin.random.Random(color.value.toLong() xor 42L)
        
        // Base clara para que el color de fondo se note
        drawRect(color = color.copy(alpha = 0.3f))
        
        val strokeWidth = 16f
        val path = androidx.compose.ui.graphics.Path()
        
        // Dibujamos unos 50 trazos
        for (i in 0..50) {
            val startX = rnd.nextFloat() * size.width * 1.2f - (size.width * 0.1f)
            val startY = rnd.nextFloat() * size.height * 1.2f - (size.height * 0.1f)
            val length = rnd.nextFloat() * 100f + 40f
            val angle = -45f + (rnd.nextFloat() * 30f - 15f) 
            
            val rad = angle * Math.PI / 180.0
            val endX = startX + (Math.cos(rad) * length).toFloat()
            val endY = startY + (Math.sin(rad) * length).toFloat()
            
            path.moveTo(startX, startY)
            path.lineTo(endX, endY)
        }
        
        // Trazos marcados
        drawPath(
            path = path,
            color = color.copy(alpha = 0.7f), 
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = strokeWidth,
                cap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        )
    }
)
