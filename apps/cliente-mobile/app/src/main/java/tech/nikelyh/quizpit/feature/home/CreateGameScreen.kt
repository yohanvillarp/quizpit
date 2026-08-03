package tech.nikelyh.quizpit.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tech.nikelyh.quizpit.ui.components.SketchbookButton

@Composable
fun CreateGameScreen(
    onNavigateBack: () -> Unit
) {
    var selectedFileUri by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<android.net.Uri?>(null) }
    var isUploadComplete by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    var isGenerating by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    
    val pdfLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        selectedFileUri = uri
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x99000000)) // Fondo oscuro semi-transparente detrás de la libreta
            .padding(top = 40.dp) // Dejar espacio arriba para que parezca una libreta que sube
    ) {
        // La libreta amarilla
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .drawBehind {
                    // Fondo amarillo pálido
                    drawRect(color = Color(0xFFFFFDE7))
                    
                    // Líneas rayadas azules de cuaderno legal
                    val lineHeight = 30.dp.toPx()
                    var y = lineHeight * 2
                    while (y < size.height) {
                        drawLine(
                            color = Color(0xFF64B5F6).copy(alpha = 0.5f),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = 2f
                        )
                        y += lineHeight
                    }

                    // Margen rojo izquierdo
                    drawLine(
                        color = Color(0xFFE57373).copy(alpha = 0.6f),
                        start = Offset(size.width * 0.15f, 0f),
                        end = Offset(size.width * 0.15f, size.height),
                        strokeWidth = 2f
                    )
                    drawLine(
                        color = Color(0xFFE57373).copy(alpha = 0.6f),
                        start = Offset(size.width * 0.15f + 4f, 0f),
                        end = Offset(size.width * 0.15f + 4f, size.height),
                        strokeWidth = 1f
                    )

                    // Borde irregular del papel
                    val path = Path().apply {
                        moveTo(0f, 0f)
                        lineTo(size.width, 0f)
                        lineTo(size.width, size.height)
                        lineTo(0f, size.height)
                        close()
                    }
                    drawPath(
                        path = path,
                        color = Color(0xFF1E1E1E),
                        style = Stroke(
                            width = 6f,
                            cap = StrokeCap.Round
                        )
                    )
                }
        ) {
            // Washi Tape arriba
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-10).dp)
                    .width(120.dp)
                    .height(30.dp)
                    .drawBehind {
                        drawRect(color = Color(0xFFFFCC80).copy(alpha = 0.8f))
                        val tapePath = Path().apply {
                            moveTo(0f, 0f)
                            lineTo(size.width, 0f)
                            lineTo(size.width, size.height)
                            lineTo(0f, size.height)
                            close()
                        }
                        drawPath(
                            path = tapePath,
                            color = Color.Black.copy(alpha = 0.3f),
                            style = Stroke(width = 2f)
                        )
                    }
            )

            // Botón "X" de cerrar (estilo garabato)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(40.dp)
                    .clickable { onNavigateBack() }
                    .drawBehind {
                        drawLine(
                            color = Color.Black,
                            start = Offset(8.dp.toPx(), 8.dp.toPx()),
                            end = Offset(size.width - 8.dp.toPx(), size.height - 8.dp.toPx()),
                            strokeWidth = 8f,
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = Color.Black,
                            start = Offset(size.width - 8.dp.toPx(), 8.dp.toPx()),
                            end = Offset(8.dp.toPx(), size.height - 8.dp.toPx()),
                            strokeWidth = 8f,
                            cap = StrokeCap.Round
                        )
                    }
            )

            // Contenido del Formulario
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 64.dp, end = 24.dp, top = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "NUEVA PARTIDA",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                tech.nikelyh.quizpit.ui.components.SketchbookUploadBox(
                    selectedFileUri = selectedFileUri,
                    onClick = { pdfLauncher.launch("application/pdf") },
                    onClearFile = { 
                        selectedFileUri = null
                        isUploadComplete = false 
                    },
                    onUploadComplete = {
                        isUploadComplete = true
                    }
                )

                androidx.compose.animation.AnimatedVisibility(
                    visible = isUploadComplete,
                    enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.expandVertically(),
                    exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.shrinkVertically()
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Spacer(modifier = Modifier.height(32.dp))
                        SketchbookButton(
                            onClick = { isGenerating = true },
                            backgroundColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Text(
                                text = "CREAR",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }
    }

    androidx.compose.animation.AnimatedVisibility(
        visible = isGenerating,
        enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.scaleIn(initialScale = 0.8f),
        exit = androidx.compose.animation.fadeOut()
    ) {
        GeneratingQuizOverlay(
            pdfUri = selectedFileUri,
            onNavigateBack = onNavigateBack
        )
    }
}
