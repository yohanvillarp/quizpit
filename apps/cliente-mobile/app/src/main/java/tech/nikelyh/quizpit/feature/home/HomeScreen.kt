package tech.nikelyh.quizpit.feature.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.geometry.Offset
import kotlin.math.roundToInt
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
import androidx.compose.ui.zIndex
import tech.nikelyh.quizpit.R
import tech.nikelyh.quizpit.ui.components.SketchbookButton
import tech.nikelyh.quizpit.ui.components.SketchbookTextField
import tech.nikelyh.quizpit.ui.components.sketchbookBackground
import tech.nikelyh.quizpit.ui.components.sketchbookBounceIn

import androidx.lifecycle.viewmodel.compose.viewModel
import tech.nikelyh.quizpit.feature.profile.ProfileViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CardGiftcard
import androidx.compose.material3.Icon
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.draw.clipToBounds

@Composable
fun HomeScreen(
    onNavigateToCreateGame: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    profileViewModel: ProfileViewModel = viewModel()
) {
    var pin by remember { mutableStateOf("") }
    var isDraggingItem by remember { mutableStateOf(false) }
    val context = androidx.compose.ui.platform.LocalContext.current

    val profileState by profileViewModel.uiState.collectAsState()
    var showGiftDialog by remember { mutableStateOf(false) }
    var giftOffset by remember { mutableStateOf(Offset(0f, 0f)) }

    // Deferred UI for better startup performance
    var isUiReady by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(200)
        isUiReady = true
    }

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

        if (showGiftDialog) {
            AlertDialog(
                onDismissRequest = { showGiftDialog = false },
                title = { Text(stringResource(R.string.home_gift_title), fontWeight = FontWeight.Bold, color = Color(0xFF1a1f3a)) },
                text = { Text(stringResource(R.string.home_gift_desc), color = Color(0xFF1a1f3a)) },
                confirmButton = {
                    TextButton(onClick = {
                        showGiftDialog = false
                        onNavigateToProfile()
                    }) {
                        Text(stringResource(R.string.home_gift_btn_link), fontWeight = FontWeight.Black, color = Color(0xFF1a1f3a))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showGiftDialog = false }) {
                        Text(stringResource(R.string.home_gift_btn_later), color = Color(0xFF1a1f3a).copy(alpha = 0.6f))
                    }
                },
                containerColor = Color(0xFFFAF9F5),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp)
            )
        }

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
                    val monsterBoundsY = -(boardHeightPx - monsterSizePx) .. 0f

                    // Pegatina del Regalo (Atrás de todo, a la derecha del monstruo)
                    if (profileState.isGuest && isUiReady) {
                        val giftSizePx = with(density) { 56.dp.toPx() }
                        // Alineación TopStart para posicionamiento absoluto exacto
                        val giftBaseXPx = boardWidthPx - giftSizePx - with(density) { 16.dp.toPx() }
                        val giftBaseYPx = boardHeightPx - giftSizePx - with(density) { 40.dp.toPx() }

                        val giftBoundsX = -giftBaseXPx .. (boardWidthPx - giftSizePx - giftBaseXPx)
                        val giftBoundsY = -giftBaseYPx .. (boardHeightPx - giftSizePx - giftBaseYPx)

                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(
                                    start = with(density) { giftBaseXPx.toDp() },
                                    top = with(density) { giftBaseYPx.toDp() }
                                )
                                .offset { IntOffset(giftOffset.x.roundToInt(), giftOffset.y.roundToInt()) }
                                .size(56.dp)
                                .background(Color(0xFFfcdc4d), CircleShape)
                                .border(3.dp, Color(0xFF1a1f3a), CircleShape)
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDragStart = { isDraggingItem = true },
                                        onDragEnd = { isDraggingItem = false },
                                        onDragCancel = { isDraggingItem = false }
                                    ) { change, dragAmount ->
                                        change.consume()
                                        val newX = (giftOffset.x + dragAmount.x).coerceIn(giftBoundsX)
                                        val newY = (giftOffset.y + dragAmount.y).coerceIn(giftBoundsY)
                                        giftOffset = Offset(newX, newY)
                                    }
                                }
                                .clickable { showGiftDialog = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.CardGiftcard,
                                contentDescription = "Gift",
                                tint = Color(0xFF1a1f3a),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    // Pegatina Izquierda: Eventos
                    if (isUiReady) {
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
                    }

                    // Pegatina Derecha: Historial
                    if (isUiReady) {
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
                    }

                    // Logo Interactivo
                    if (isUiReady) {
                        tech.nikelyh.quizpit.ui.components.InteractiveMonsterLogo(
                            onDragStateChanged = { isDraggingItem = it },
                            dragBoundsX = monsterBoundsX,
                            dragBoundsY = monsterBoundsY,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .sketchbookBounceIn()
                        )
                    }
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
                        tech.nikelyh.quizpit.ui.components.GamePinTextField(
                            value = pin,
                            onValueChange = { pin = it },
                            placeholder = stringResource(id = R.string.home_game_pin_hint)
                        )

                        // Join Button
                        val isPinValid = pin.length == 6
                        SketchbookButton(
                            onClick = { 
                                if (tech.nikelyh.quizpit.core.domain.EnergyManager.inkDrops.value > 0) {
                                    tech.nikelyh.quizpit.core.domain.EnergyManager.spendInk()
                                    android.widget.Toast.makeText(context, "Buscando partida...", android.widget.Toast.LENGTH_SHORT).show()
                                } else {
                                    android.widget.Toast.makeText(context, "¡No tienes suficientes Gotas de Tinta!", android.widget.Toast.LENGTH_LONG).show()
                                }
                            },
                            enabled = isPinValid,
                            backgroundColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.fillMaxWidth()
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
                            onClick = {
                                if (tech.nikelyh.quizpit.core.domain.EnergyManager.inkDrops.value > 0) {
                                    onNavigateToCreateGame()
                                } else {
                                    android.widget.Toast.makeText(context, "¡No tienes suficientes Gotas de Tinta!", android.widget.Toast.LENGTH_LONG).show()
                                }
                            },
                            backgroundColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.fillMaxWidth()
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

        // Currency Top Bar (Floating)
        tech.nikelyh.quizpit.ui.components.CurrencyTopBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .zIndex(10f)
        )
    }
}
