package tech.nikelyh.quizpit.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import tech.nikelyh.quizpit.ui.components.SketchyAIRobot
import tech.nikelyh.quizpit.ui.components.sketchbookBackground

@Composable
fun GeneratingQuizOverlay() {
    var dotsCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(500)
            dotsCount = (dotsCount + 1) % 4
        }
    }

    val dots = ".".repeat(dotsCount)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFDE7)) // Fondo libreta
            .sketchbookBackground(gridColor = Color(0xFF64B5F6).copy(alpha = 0.3f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            SketchyAIRobot()
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "GENERANDO PREGUNTAS$dots",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF1E1E1E),
                modifier = Modifier.width(300.dp), // Fijo para que los puntos no empujen el texto bruscamente
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
