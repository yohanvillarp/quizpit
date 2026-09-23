package tech.nikelyh.quizpit.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import tech.nikelyh.quizpit.R
import tech.nikelyh.quizpit.core.domain.EnergyManager

@Composable
fun InkCounterBadge(modifier: Modifier = Modifier) {
    val inkDrops by EnergyManager.inkDrops.collectAsState()
    
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFFAF9F5)) // Paper
            .sketchbookColoring(Color(0xFFf4d7e8)) // HighPink
            .border(2.dp, Color(0xFF1a1f3a), RoundedCornerShape(12.dp)) // Navy
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_ink),
            contentDescription = null,
            modifier = Modifier.size(20.dp).padding(end = 4.dp)
        )
        Text(
            text = inkDrops.toString(),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Black),
            color = Color(0xFF1a1f3a)
        )
    }
}
