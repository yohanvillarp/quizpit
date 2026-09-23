package tech.nikelyh.quizpit.feature.roulette.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import tech.nikelyh.quizpit.R
import tech.nikelyh.quizpit.feature.roulette.domain.PrizeType
import kotlin.math.cos
import kotlin.math.sin

private val Navy = Color(0xFF1a1f3a)

@Composable
fun RouletteWheel(
    prizes: List<PrizeType>,
    isSpinning: Boolean,
    targetPrize: PrizeType?,
    onSpinComplete: (PrizeType) -> Unit,
    modifier: Modifier = Modifier
) {
    val numSlices = prizes.size
    val sweepAngle = 360f / numSlices
    val haptic = LocalHapticFeedback.current
    val textMeasurer = rememberTextMeasurer()

    // Usamos un Animatable para tener control total de la física
    val rotation = remember { Animatable(0f) }

    // Sincronización del sonido/vibración
    var lastTickAngle by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(isSpinning) {
        if (isSpinning && targetPrize != null) {
            val targetIndex = prizes.indexOfFirst { it == targetPrize }

            // Ángulo objetivo: el centro de la rebanada ganadora debe estar en el marcador (arriba, 270 deg)
            val baseTargetAngle = 270f - (targetIndex * sweepAngle) - (sweepAngle / 2f)

            // Añadimos múltiples vueltas (Mínimo 8 para que se vea rápido al inicio)
            val fullSpins = 8
            val finalRotation = rotation.value + (fullSpins * 360f) + (baseTargetAngle - (rotation.value % 360f))

            // Animación de Giro Realista (Aceleración fuerte -> Desaceleración lenta)
            rotation.animateTo(
                targetValue = finalRotation,
                animationSpec = tween(
                    durationMillis = 6000,
                    easing = CubicBezierEasing(0.12f, 0.8f, 0.32f, 1f)
                )
            ) {
                // Ticks de vibración al pasar por cada clavo
                val currentAngle = value % 360f
                if (Math.abs(currentAngle - lastTickAngle) >= sweepAngle) {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    lastTickAngle = currentAngle
                }
            }

            // Rebote final elástico
            rotation.animateTo(
                targetValue = finalRotation + 3f,
                animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)
            )
            rotation.animateTo(finalRotation, animationSpec = spring())

            onSpinComplete(targetPrize)
        }
    }

    val prizePainters = prizes.map { prize ->
        prize.iconRes?.let { resId ->
            rememberVectorPainter(image = ImageVector.vectorResource(id = resId))
        }
    }

    Box(contentAlignment = Alignment.Center, modifier = modifier.size(340.dp)) {

        // 1. Sombra de la base
        Canvas(modifier = Modifier.fillMaxSize().offset(y = 12.dp)) {
            drawCircle(color = Navy.copy(alpha = 0.08f), radius = size.minDimension / 2.1f)
        }

        // 2. Marcador superior (Estilo "Pin" de mapa)
        IconMarker(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-15).dp)
                .zIndex(10f)
        )

        // 3. La Ruleta Física
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { rotationZ = rotation.value }
        ) {
            val radius = size.minDimension / 2 - 20.dp.toPx()
            val center = Offset(size.width / 2, size.height / 2)

            // Dibujar cada segmento
            for (i in 0 until numSlices) {
                val startAngle = i * sweepAngle

                // Color de fondo del premio
                val path = Path().apply {
                    moveTo(center.x, center.y)
                    arcTo(
                        rect = Size(radius * 2, radius * 2).let { s ->
                            androidx.compose.ui.geometry.Rect(center.x - radius, center.y - radius, center.x + radius, center.y + radius)
                        },
                        startAngleDegrees = startAngle,
                        sweepAngleDegrees = sweepAngle,
                        forceMoveTo = false
                    )
                    close()
                }

                drawPath(path = path, color = prizes[i].color)

                // Borde de tinta de cada rebanada
                drawPath(
                    path = path,
                    color = Navy.copy(alpha = 0.6f),
                    style = Stroke(width = 2f)
                )

                // Clavos metálicos en el borde de cada segmento
                val nailAngleRad = Math.toRadians(startAngle.toDouble())
                val nailPos = Offset(
                    center.x + radius * cos(nailAngleRad).toFloat(),
                    center.y + radius * sin(nailAngleRad).toFloat()
                )
                drawCircle(color = Navy, radius = 4f, center = nailPos)
            }

            // Borde exterior grueso (Círculo de la rueda)
            drawCircle(
                color = Navy,
                radius = radius,
                center = center,
                style = Stroke(width = 8f)
            )

            // Dibujar Contenido (Iconos y Nombres)
            for (i in 0 until numSlices) {
                val angleInRadians = Math.toRadians((i * sweepAngle + sweepAngle / 2).toDouble())
                val contentRadius = radius * 0.68f

                val x = center.x + contentRadius * cos(angleInRadians).toFloat()
                val y = center.y + contentRadius * sin(angleInRadians).toFloat()

                val prize = prizes[i]
                val painter = prizePainters[i]

                rotate(degrees = i * sweepAngle + sweepAngle / 2 + 90f, pivot = Offset(x, y)) {
                    if (painter != null) {
                        val iconSize = 42.dp.toPx()
                        translate(left = x - iconSize / 2, top = y - iconSize / 2 - 18.dp.toPx()) {
                            with(painter) { draw(size = Size(iconSize, iconSize)) }
                        }
                    }

                    val textLayoutResult = textMeasurer.measure(
                        text = prize.shortName,
                        style = TextStyle(
                            color = Navy,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                    )

                    drawText(
                        textLayoutResult = textLayoutResult,
                        topLeft = Offset(x - textLayoutResult.size.width / 2, y + 6.dp.toPx())
                    )
                }
            }
        }

        // 4. Centro Decorativo (Eje)
        Box(
            modifier = Modifier
                .size(54.dp)
                .zIndex(5f),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(color = Navy, radius = 24.dp.toPx())
                drawCircle(color = Color.White, radius = 20.dp.toPx())
                drawCircle(color = Navy, radius = 6.dp.toPx())
            }
        }
    }
}

@Composable
fun IconMarker(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(44.dp)) {
        val path = Path().apply {
            moveTo(size.width / 2, size.height) // Punta abajo
            lineTo(size.width * 0.1f, size.height * 0.2f)
            quadraticBezierTo(size.width / 2, -10f, size.width * 0.9f, size.height * 0.2f)
            close()
        }

        // Borde
        drawPath(
            path = path,
            color = Navy,
            style = Stroke(width = 6f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
        // Relleno
        drawPath(
            path = path,
            color = Color(0xFFEF5350)
        )
    }
}
