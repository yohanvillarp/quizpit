package tech.nikelyh.quizpit.feature.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import tech.nikelyh.quizpit.R
import tech.nikelyh.quizpit.ui.components.sketchbookBackground

// Colors matching Analog Sketchbook design
private val Navy = Color(0xFF1a1f3a)
private val Paper = Color(0xFFFAF9F5)
private val HostYellow = Color(0xFFffeb99)

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit,
    viewModel: SplashViewModel = viewModel()
) {
    val isLoaded by viewModel.isLoaded.collectAsState()
    val loadingProgress by viewModel.loadingProgress.collectAsState()

    LaunchedEffect(isLoaded) {
        if (isLoaded) {
            onSplashFinished()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Paper)
            .sketchbookBackground(gridColor = Navy.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            // Logo Image (we use the png)
            Image(
                painter = painterResource(id = R.drawable.logo_quizpit),
                contentDescription = "QuizPit Logo",
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Loading Text
            Text(
                text = "SINCRONIZANDO PODERES...",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Navy.copy(alpha = 0.7f),
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Hand-drawn Loading Bar Frame
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .border(3.dp, Navy, RoundedCornerShape(12.dp))
                    .padding(3.dp)
            ) {
                // Animated inner bar
                val animatedProgress by animateFloatAsState(
                    targetValue = loadingProgress,
                    animationSpec = tween(durationMillis = 300),
                    label = "progress"
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .background(HostYellow)
                        .border(2.dp, Navy, RoundedCornerShape(8.dp))
                )
            }
        }
    }
}
