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

import kotlinx.coroutines.launch

@Composable
fun CreateGameScreen(
    onNavigateBack: () -> Unit,
    onNavigateToLobby: (String) -> Unit
) {
    var selectedFileUri by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf<android.net.Uri?>(null) }
    var isUploadComplete by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    var isGenerating by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(false) }
    var selectedMode by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("NORMAL") }
    val context = androidx.compose.ui.platform.LocalContext.current
    val scope = androidx.compose.runtime.rememberCoroutineScope()

    val pdfLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        if (uri != null) {
            // Reset states before validation to avoid UI inconsistencies
            selectedFileUri = null
            isUploadComplete = false

            scope.launch {
                val validator = tech.nikelyh.quizpit.core.infrastructure.file.AndroidFileValidatorImpl(context)
                val result = validator.validatePdf(uri)
                if (result.isSuccess) {
                    selectedFileUri = uri
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Error al validar archivo"
                    android.widget.Toast.makeText(context, error, android.widget.Toast.LENGTH_LONG).show()
                }
            }
        }
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
                    .padding(start = 64.dp, end = 24.dp, top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "NUEVA PARTIDA",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color(0xFF1a1f3a),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                tech.nikelyh.quizpit.ui.components.InkCounterBadge(
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
                        Spacer(modifier = Modifier.height(24.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
                        ) {
                            SketchbookButton(
                                onClick = { selectedMode = "NORMAL" },
                                backgroundColor = if (selectedMode == "NORMAL") MaterialTheme.colorScheme.secondary else Color(0xFFFAF9F5),
                                contentColor = if (selectedMode == "NORMAL") MaterialTheme.colorScheme.onSecondary else Color(0xFF1a1f3a),
                                modifier = Modifier.weight(1f).aspectRatio(1f)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    tech.nikelyh.quizpit.ui.components.ModeIcon(
                                        isPower = false,
                                        tint = if (selectedMode == "NORMAL") MaterialTheme.colorScheme.onSecondary else Color(0xFF1a1f3a),
                                        modifier = Modifier.size(32.dp).padding(bottom = 8.dp)
                                    )
                                    Text(
                                        text = androidx.compose.ui.res.stringResource(id = tech.nikelyh.quizpit.R.string.create_game_mode_normal),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                            SketchbookButton(
                                onClick = { selectedMode = "POWER" },
                                backgroundColor = if (selectedMode == "POWER") MaterialTheme.colorScheme.secondary else Color(0xFFFAF9F5),
                                contentColor = if (selectedMode == "POWER") MaterialTheme.colorScheme.onSecondary else Color(0xFF1a1f3a),
                                modifier = Modifier.weight(1f).aspectRatio(1f)
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    tech.nikelyh.quizpit.ui.components.ModeIcon(
                                        isPower = true,
                                        tint = if (selectedMode == "POWER") MaterialTheme.colorScheme.onSecondary else Color(0xFF1a1f3a),
                                        modifier = Modifier.size(32.dp).padding(bottom = 8.dp)
                                    )
                                    Text(
                                        text = androidx.compose.ui.res.stringResource(id = tech.nikelyh.quizpit.R.string.create_game_mode_power),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        }

                        SketchbookButton(
                            onClick = {
                                if (tech.nikelyh.quizpit.core.domain.EnergyManager.spendInk()) {
                                    isGenerating = true
                                }
                            },
                            backgroundColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "CREAR",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "-1",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                androidx.compose.material3.Icon(
                                    painter = androidx.compose.ui.res.painterResource(id = tech.nikelyh.quizpit.R.drawable.ic_ink),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = androidx.compose.ui.graphics.Color.Unspecified
                                )
                            }
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
            onNavigateBack = {
                isGenerating = false // Return to this screen without navigating back completely
            },
            onQuizGenerated = { roomId ->
                isGenerating = false
                onNavigateToLobby(roomId)
            }
        )
    }
}
