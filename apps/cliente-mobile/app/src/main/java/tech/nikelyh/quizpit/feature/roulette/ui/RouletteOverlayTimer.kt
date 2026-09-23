package tech.nikelyh.quizpit.feature.roulette.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tech.nikelyh.quizpit.ui.components.SketchbookButton

private val Navy = Color(0xFF1a1f3a)
private val Paper = Color(0xFFFAF9F5)
private val YellowCTA = Color(0xFFffeb99)

@Composable
fun RouletteOverlayTimer(
    hasAdSpin: Boolean,
    isSimulatingAd: Boolean,
    currentServerTimeMs: Long,
    nextResetTimeMs: Long,
    onWatchAdClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var timeLeftStr by remember { mutableStateOf("00:00:00") }

    LaunchedEffect(currentServerTimeMs, nextResetTimeMs) {
        if (nextResetTimeMs > currentServerTimeMs) {
            val diff = nextResetTimeMs - currentServerTimeMs
            val hours = (diff / (1000 * 60 * 60)) % 24
            val mins = (diff / (1000 * 60)) % 60
            val secs = (diff / 1000) % 60
            timeLeftStr = String.format("%02d:%02d:%02d", hours, mins, secs)
        } else {
            timeLeftStr = "00:00:00"
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Navy.copy(alpha = 0.8f)), // Overlay oscuro para foco
        contentAlignment = Alignment.Center
    ) {
        // Tarjeta central limpia (sin garabatos para legibilidad)
        Card(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxWidth()
                .border(4.dp, Navy, RoundedCornerShape(32.dp)),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Paper)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "PRÓXIMO GIRO",
                    color = Navy,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Contador grande y claro
                Surface(
                    color = Navy.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text(
                        text = timeLeftStr,
                        color = Navy,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Tu regalo diario se está preparando.",
                    color = Navy.copy(alpha = 0.7f),
                    fontSize = 15.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )

                if (hasAdSpin) {
                    Spacer(modifier = Modifier.height(32.dp))

                    if (isSimulatingAd) {
                        CircularProgressIndicator(color = Navy, strokeWidth = 4.dp)
                        Text(
                            text = "CARGANDO...",
                            color = Navy,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    } else {
                        SketchbookButton(
                            onClick = onWatchAdClick,
                            backgroundColor = YellowCTA,
                            contentColor = Navy,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "¡GIRO EXTRA AHORA!",
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                        }

                        Text(
                            text = "Mira un corto video para jugar de nuevo.",
                            color = Navy.copy(alpha = 0.5f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                }
            }
        }
    }
}
