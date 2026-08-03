package tech.nikelyh.quizpit.ui.components

import android.net.Uri
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import tech.nikelyh.quizpit.R
import kotlin.random.Random

enum class UploadState { IDLE, UPLOADING, UPLOADED }

@Composable
fun SketchbookUploadBox(
    selectedFileUri: Uri?,
    onClick: () -> Unit,
    onClearFile: () -> Unit,
    onUploadComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    
    var progress by remember { mutableStateOf(0f) }
    
    val state = when {
        selectedFileUri == null -> UploadState.IDLE
        progress < 100f -> UploadState.UPLOADING
        else -> UploadState.UPLOADED
    }

    LaunchedEffect(selectedFileUri) {
        if (selectedFileUri != null) {
            progress = 0f
            while (progress < 100f) {
                delay(Random.nextLong(50, 150)) // Simulación de salto irregular
                progress += Random.nextFloat() * 15f + 5f // Suma de 5 a 20%
                if (progress >= 100f) {
                    progress = 100f
                    onUploadComplete()
                }
            }
        } else {
            progress = 0f
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
            .clickable(enabled = state == UploadState.IDLE) { onClick() }
            .drawBehind {
                // Sombra desfasada
                drawRoundRect(
                    color = Color.Black.copy(alpha = 0.15f),
                    topLeft = androidx.compose.ui.geometry.Offset(8.dp.toPx(), 8.dp.toPx()),
                    size = size,
                    cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx())
                )
                
                // Fondo
                drawRoundRect(
                    color = backgroundColor,
                    size = size,
                    cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx())
                )

                // Borde punteado
                val stroke = Stroke(
                    width = 4f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 20f), 0f)
                )
                drawRoundRect(
                    color = Color.Black.copy(alpha = 0.6f),
                    size = size,
                    cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
                    style = stroke
                )
            },
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(targetState = state, label = "UploadState") { currentState ->
            when (currentState) {
                UploadState.IDLE -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_pdf),
                            contentDescription = "Upload PDF",
                            modifier = Modifier.size(110.dp)
                        )
                        Text(
                            text = "Toca para subir tu PDF",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "QuizPit procesará tus apuntes\ny generará el quiz automáticamente.",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                UploadState.UPLOADING -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(horizontal = 32.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_pdf),
                            contentDescription = "Uploading PDF",
                            modifier = Modifier.size(80.dp),
                            alpha = 0.5f
                        )
                        Text(
                            text = "Leyendo archivo...",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black.copy(alpha = 0.8f)
                        )
                        
                        // Barra de progreso dibujada a mano
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(24.dp)
                                .drawBehind {
                                    val strokeWidth = 12f
                                    val yOffset = size.height / 2
                                    // Fondo de la barra
                                    drawLine(
                                        color = Color.Black.copy(alpha = 0.2f),
                                        start = androidx.compose.ui.geometry.Offset(0f, yOffset),
                                        end = androidx.compose.ui.geometry.Offset(size.width, yOffset),
                                        strokeWidth = strokeWidth,
                                        cap = StrokeCap.Round
                                    )
                                    // Progreso
                                    val progressWidth = size.width * (progress / 100f)
                                    drawLine(
                                        color = Color(0xFFE57373), // Rojo marcador
                                        start = androidx.compose.ui.geometry.Offset(0f, yOffset + Random.nextFloat() * 2f - 1f), // Trazos imperfectos
                                        end = androidx.compose.ui.geometry.Offset(progressWidth, yOffset + Random.nextFloat() * 2f - 1f),
                                        strokeWidth = strokeWidth,
                                        cap = StrokeCap.Round
                                    )
                                }
                        )
                        Text(
                            text = "${progress.toInt()}%",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black.copy(alpha = 0.6f)
                        )
                    }
                }
                UploadState.UPLOADED -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_pdf),
                            contentDescription = "Uploaded PDF",
                            modifier = Modifier.size(90.dp)
                        )
                        Text(
                            text = "¡Archivo listo!",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "Generando preguntas (PRÓXIMAMENTE)",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center
                        )
                    }

                    // Botón "X" de cancelar archivo
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .size(32.dp)
                            .clickable { onClearFile() }
                            .drawBehind {
                                drawLine(
                                    color = Color.Black.copy(alpha = 0.6f),
                                    start = androidx.compose.ui.geometry.Offset(4.dp.toPx(), 4.dp.toPx()),
                                    end = androidx.compose.ui.geometry.Offset(size.width - 4.dp.toPx(), size.height - 4.dp.toPx()),
                                    strokeWidth = 6f,
                                    cap = StrokeCap.Round
                                )
                                drawLine(
                                    color = Color.Black.copy(alpha = 0.6f),
                                    start = androidx.compose.ui.geometry.Offset(size.width - 4.dp.toPx(), 4.dp.toPx()),
                                    end = androidx.compose.ui.geometry.Offset(4.dp.toPx(), size.height - 4.dp.toPx()),
                                    strokeWidth = 6f,
                                    cap = StrokeCap.Round
                                )
                            }
                    )
                }
            }
        }
    }
}
