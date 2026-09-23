package tech.nikelyh.quizpit.feature.roulette.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import tech.nikelyh.quizpit.feature.roulette.domain.PrizeType
import tech.nikelyh.quizpit.feature.roulette.domain.PrizeWheel
import java.util.Locale

private val Navy = Color(0xFF1a1f3a)
private val Paper = Color(0xFFFAF9F5)

@Composable
fun PrizeProbabilitiesDialog(
    pityBonus: Int,
    isMythicUnlocked: Boolean = false,
    onDismiss: () -> Unit
) {
    val wheel = PrizeWheel(isMythicUnlocked)
    val dynamicWeights = wheel.getDynamicWeights(pityBonus)
    val totalWeight = dynamicWeights.sum().toFloat()

    val distinctPrizes = PrizeType.entries.filter {
        if (it == PrizeType.MYTHIC_FAMILIAR) !isMythicUnlocked else true
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .border(4.dp, Navy, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Paper)
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "PROBABILIDADES",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            ),
                            color = Navy
                        )
                        if (pityBonus > 0) {
                            Text(
                                text = "BONUS DE SUERTE: +$pityBonus",
                                color = Color(0xFF4CAF50),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Rounded.Close, contentDescription = "Close", tint = Navy)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(distinctPrizes) { prize ->
                        val occurrences = wheel.items.count { it == prize }
                        if (occurrences > 0) {
                            val prizeIndexInWheel = wheel.items.indexOf(prize)
                            val weight = dynamicWeights[prizeIndexInWheel]
                            val probability = (weight.toFloat() * occurrences / totalWeight) * 100
                            ProbabilityRow(prize, probability)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Tu probabilidad de premios raros aumenta con cada intento fallido.",
                    style = MaterialTheme.typography.labelSmall,
                    color = Navy.copy(alpha = 0.5f),
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
private fun ProbabilityRow(prize: PrizeType, probability: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(prize.color.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            .border(1.dp, Navy.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(prize.color.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .border(1.dp, Navy.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (prize.iconRes != null) {
                Icon(
                    painter = painterResource(id = prize.iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.Unspecified
                )
            } else {
                Text("?", fontWeight = FontWeight.Black, color = Navy)
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = prize.displayName,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = Navy
        )

        Text(
            text = String.format(Locale.getDefault(), "%.1f%%", probability),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Black),
            color = Navy
        )
    }
}
