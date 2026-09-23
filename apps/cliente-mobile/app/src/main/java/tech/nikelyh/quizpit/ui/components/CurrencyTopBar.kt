package tech.nikelyh.quizpit.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tech.nikelyh.quizpit.R
import tech.nikelyh.quizpit.ui.components.sketchbookColoring

// Analog Sketchbook Colors
private val Navy = Color(0xFF1a1f3a)
private val HighPink = Color(0xFFf4d7e8)
private val Paper = Color(0xFFFAF9F5)
private val CyanAccent = Color(0xFF99F5E5)

@Composable
fun CurrencyTopBar(
    modifier: Modifier = Modifier
) {
    // Intentionally left blank. The currency is now shown in the bottom bar badge.
}

@Composable
private fun CurrencyBadge(iconRes: Int, amount: String, bgColor: Color) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Paper)
            .sketchbookColoring(bgColor)
            .border(2.dp, Navy, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(20.dp).padding(end = 4.dp)
        )
        Text(
            text = amount,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Black),
            color = Navy
        )
    }
}
