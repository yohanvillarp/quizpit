package tech.nikelyh.quizpit.feature.roulette

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.SentimentVeryDissatisfied
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import tech.nikelyh.quizpit.R
import tech.nikelyh.quizpit.feature.roulette.domain.PrizeWheel
import tech.nikelyh.quizpit.feature.roulette.domain.PrizeType
import tech.nikelyh.quizpit.feature.roulette.ui.PrizeProbabilitiesDialog
import tech.nikelyh.quizpit.feature.roulette.ui.RouletteOverlayTimer
import tech.nikelyh.quizpit.feature.roulette.ui.RouletteWheel
import tech.nikelyh.quizpit.ui.components.SketchbookButton
import tech.nikelyh.quizpit.ui.components.sketchbookBackground
import tech.nikelyh.quizpit.ui.components.sketchbookBounceIn

private val Navy = Color(0xFF1a1f3a)
private val YellowCTA = Color(0xFFffeb99)
private val Paper = Color(0xFFFAF9F5)

@Composable
fun RouletteScreen(
    viewModel: RouletteViewModel = viewModel()
) {
    val hasFreeSpin by viewModel.hasFreeSpin.collectAsState()
    val hasAdSpin by viewModel.hasAdSpin.collectAsState()
    val nextResetTimeMs by viewModel.nextResetTimeMs.collectAsState()
    val currentServerTimeMs by viewModel.currentServerTimeMs.collectAsState()
    val isSimulatingAd by viewModel.isSimulatingAd.collectAsState()
    val prizeObtained by viewModel.prizeObtained.collectAsState()
    val pityBonus by viewModel.spinsWithoutRarePrize.collectAsState()
    val isMythicUnlocked by viewModel.isMythicUnlocked.collectAsState()

    val prizeWheel = remember(isMythicUnlocked) { PrizeWheel(isMythicUnlocked) }
    var isSpinning by remember { mutableStateOf(false) }
    var targetPrize by remember { mutableStateOf<PrizeType?>(null) }
    var showPrizeDialog by remember { mutableStateOf(false) }
    var showProbabilities by remember { mutableStateOf(false) }

    // Sincronizar el diálogo con el fin de la animación
    LaunchedEffect(prizeObtained) {
        if (prizeObtained != null && !isSpinning) {
            showPrizeDialog = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Paper)
            .sketchbookBackground()
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(top = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título Mítico
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "RULETA MÍTICA",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 2.sp
                    ),
                    color = Navy
                )
                Surface(
                    color = Navy,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = "SUERTE DIARIA",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Área de la Ruleta (Crecimiento dinámico)
            Box(
                modifier = Modifier.fillMaxWidth().padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                RouletteWheel(
                    prizes = prizeWheel.items,
                    isSpinning = isSpinning,
                    targetPrize = targetPrize,
                    onSpinComplete = {
                        isSpinning = false
                        viewModel.onSpinComplete(it)
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Botón Principal
            Box(
                modifier = Modifier
                    .padding(bottom = 80.dp)
                    .height(90.dp),
                contentAlignment = Alignment.Center
            ) {
                val canSpin = hasFreeSpin || (hasAdSpin && isSimulatingAd)

                if (canSpin || isSpinning) {
                    SketchbookButton(
                        onClick = {
                            if (!isSpinning && canSpin) {
                                targetPrize = prizeWheel.spin(pityBonus)
                                isSpinning = true
                            }
                        },
                        backgroundColor = YellowCTA,
                        contentColor = Navy,
                        modifier = Modifier
                            .width(260.dp)
                            .sketchbookBounceIn()
                    ) {
                        Text(
                            text = if (isSpinning) "¡GIRANDO...!" else "GIRAR AHORA",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // Icono de información de probabilidades (Top End)
        IconButton(
            onClick = { showProbabilities = true },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 12.dp, end = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Info,
                contentDescription = "Probabilidades",
                tint = Navy.copy(alpha = 0.4f)
            )
        }

        // Diálogo de Probabilidades
        if (showProbabilities) {
            PrizeProbabilitiesDialog(
                pityBonus = pityBonus,
                isMythicUnlocked = isMythicUnlocked,
                onDismiss = { showProbabilities = false }
            )
        }

        // Overlay de Bloqueo / Timer
        if (!hasFreeSpin && !isSpinning && !isSimulatingAd) {
            RouletteOverlayTimer(
                hasAdSpin = hasAdSpin,
                isSimulatingAd = isSimulatingAd,
                currentServerTimeMs = currentServerTimeMs,
                nextResetTimeMs = nextResetTimeMs,
                onWatchAdClick = { viewModel.simulateAd() }
            )
        }

        // Diálogo de Premio (Limpio y Premium)
        if (showPrizeDialog && prizeObtained != null) {
            PrizeObtainedDialog(
                prize = prizeObtained!!,
                onDismiss = {
                    showPrizeDialog = false
                    viewModel.clearPrize()
                }
            )
        }
    }
}

@Composable
fun PrizeObtainedDialog(
    prize: PrizeType,
    onDismiss: () -> Unit
) {
    val isNothing = prize == PrizeType.NOTHING

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(4.dp, Navy, RoundedCornerShape(32.dp)),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Paper)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = if (isNothing) Icons.Rounded.SentimentVeryDissatisfied else Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = Navy,
                    modifier = Modifier.size(40.dp)
                )

                Text(
                    text = if (isNothing) "¡MALA SUERTE!" else "¡FELICIDADES!",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black),
                    color = Navy
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Círculo del Premio (Color del premio como fondo suave)
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .background(prize.color.copy(alpha = 0.2f), RoundedCornerShape(65.dp))
                        .border(3.dp, Navy, RoundedCornerShape(65.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (prize.iconRes != null) {
                        Icon(
                            painter = painterResource(id = prize.iconRes),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            tint = Color.Unspecified
                        )
                    } else {
                        // Icono de "Suerte" vacío o interrogación
                        Text("?", fontSize = 48.sp, fontWeight = FontWeight.Black, color = Navy.copy(alpha = 0.2f))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = prize.displayName,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black
                    ),
                    color = Navy,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                SketchbookButton(
                    onClick = onDismiss,
                    backgroundColor = if (isNothing) Paper else YellowCTA,
                    contentColor = Navy,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isNothing) "CONTINUAR" else "RECLAMAR PREMIO",
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}
