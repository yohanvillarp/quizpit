package tech.nikelyh.quizpit.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tech.nikelyh.quizpit.R
import tech.nikelyh.quizpit.ui.components.SketchbookButton
import tech.nikelyh.quizpit.ui.components.SketchbookTextField
import tech.nikelyh.quizpit.ui.components.sketchbookBackground
import tech.nikelyh.quizpit.ui.components.sketchbookBounceIn

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.draw.clipToBounds

@Composable
fun HomeScreen(
    onNavigateToCreateGame: () -> Unit = {}
) {
    var pin by remember { mutableStateOf("") }
    var isDraggingItem by remember { mutableStateOf(false) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .sketchbookBackground() 
    ) {
        val screenHeight = maxHeight
        // 50% arriba y 50% abajo para que sea totalmente equitativo en todos los dispositivos
        val topHalfHeight = screenHeight * 0.50f
        val bottomHalfHeight = screenHeight * 0.50f
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.heightIn(min = screenHeight), 
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // --- MITAD SUPERIOR: Pizarra Libre ---
                BoxWithConstraints(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(topHalfHeight)
                ) {
                    val density = LocalDensity.current
                    val boardWidthPx = constraints.maxWidth.toFloat()
                    val boardHeightPx = constraints.maxHeight.toFloat()
                    
                    val stickerSizePx = with(density) { 100.dp.toPx() } // Tamaño aproximado de la pegatina
                    val monsterSizePx = with(density) { 200.dp.toPx() }
                    val paddingPx = with(density) { 24.dp.toPx() }

                    // Cálculos matemáticos deterministas y absolutos
                    val leftStickerBoundsX = -paddingPx .. (boardWidthPx - stickerSizePx - paddingPx)
                    val leftStickerBoundsY = -paddingPx .. (boardHeightPx - stickerSizePx - paddingPx)

                    val rightStickerBoundsX = -(boardWidthPx - stickerSizePx - paddingPx) .. paddingPx
                    val rightStickerBoundsY = -paddingPx .. (boardHeightPx - stickerSizePx - paddingPx)

                    val monsterBoundsX = -(boardWidthPx / 2 - monsterSizePx / 2 + monsterSizePx * 0.2f) .. (boardWidthPx / 2 - monsterSizePx / 2 + monsterSizePx * 0.2f)
                    // Límite inferior en 0f para que se detenga exactamente en la misma línea que las pegatinas
                    val monsterBoundsY = -(boardHeightPx - monsterSizePx) .. 0f

                    // Pegatina Izquierda: Eventos
                    tech.nikelyh.quizpit.ui.components.SketchbookStickerButton(
                        icon = painterResource(id = R.drawable.ic_events),
                        text = stringResource(id = R.string.home_events),
                        rotation = -5f,
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        onClick = { /* TODO: Abrir Eventos */ },
                        onDragStateChanged = { isDraggingItem = it },
                        dragBoundsX = leftStickerBoundsX,
                        dragBoundsY = leftStickerBoundsY,
                        modifier = Modifier.align(Alignment.TopStart).padding(start = 24.dp, top = 24.dp)
                    )
                    
                    // Pegatina Derecha: Historial
                    tech.nikelyh.quizpit.ui.components.SketchbookStickerButton(
                        icon = painterResource(id = R.drawable.ic_history),
                        text = stringResource(id = R.string.home_history),
                        rotation = 4f,
                        backgroundColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary,
                        onClick = { /* TODO: Mostrar últimas 5 partidas */ },
                        onDragStateChanged = { isDraggingItem = it },
                        dragBoundsX = rightStickerBoundsX,
                        dragBoundsY = rightStickerBoundsY,
                        modifier = Modifier.align(Alignment.TopEnd).padding(end = 24.dp, top = 24.dp)
                    )
                    
                    // Logo Interactivo
                    tech.nikelyh.quizpit.ui.components.InteractiveMonsterLogo(
                        onDragStateChanged = { isDraggingItem = it },
                        dragBoundsX = monsterBoundsX,
                        dragBoundsY = monsterBoundsY,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .sketchbookBounceIn()
                    )
                }
                
                // --- MITAD INFERIOR: Formulario y Acciones ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(bottomHalfHeight),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // Zona Roja: Peligro / Barrera visual (ahora ocupa todo el ancho)
                    val redZoneAlpha by animateFloatAsState(
                        targetValue = if (isDraggingItem) 1f else 0f,
                        label = "redZoneAlpha"
                    )
                    
                    if (redZoneAlpha > 0f) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer(alpha = redZoneAlpha)
                                .background(Color.Red.copy(alpha = 0.1f))
                        ) {
                            // Línea divisoria superior
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(2.dp)
                                    .background(Color.Red.copy(alpha = 0.3f))
                                    .align(Alignment.TopCenter)
                            )
                        }
                    }

                    // Formulario con su propio padding para no chocar con los bordes
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // PIN Input
                        SketchbookTextField(
                            value = pin,
                            onValueChange = { if (it.length <= 6) pin = it.uppercase() },
                            placeholder = stringResource(id = R.string.home_game_pin_hint)
                        )

                        // Join Button
                        val isPinValid = pin.length == 6
                        SketchbookButton(
                            onClick = { /* TODO: Lógica de unirse */ },
                            enabled = isPinValid,
                            backgroundColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ) {
                            Text(
                                text = stringResource(id = R.string.home_join_button),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        // Separador O
                        Text(
                            text = stringResource(id = R.string.home_or_separator),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )

                        // Create Game Button
                        SketchbookButton(
                            onClick = onNavigateToCreateGame,
                            backgroundColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        ) {
                            Text(
                                text = stringResource(id = R.string.home_create_game_button),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }
    }
}
