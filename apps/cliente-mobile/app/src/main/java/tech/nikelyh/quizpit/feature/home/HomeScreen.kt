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

@Composable
fun HomeScreen() {
    var pin by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .sketchbookBackground() // Cuadrícula de cuaderno
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Cabecera: Botones superiores tipo "Notas Adhesivas / Pegatinas"
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Pegatina Izquierda: Eventos
            tech.nikelyh.quizpit.ui.components.SketchbookStickerButton(
                icon = painterResource(id = R.drawable.ic_events),
                text = stringResource(id = R.string.home_events),
                rotation = -5f,
                backgroundColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                onClick = { /* TODO: Abrir Eventos */ }
            )
            
            // Pegatina Derecha: Historial
            tech.nikelyh.quizpit.ui.components.SketchbookStickerButton(
                icon = painterResource(id = R.drawable.ic_history),
                text = stringResource(id = R.string.home_history),
                rotation = 4f,
                backgroundColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
                onClick = { /* TODO: Mostrar últimas 5 partidas */ }
            )
        }
        
        // Logo
        Image(
            painter = painterResource(id = R.drawable.logo_quizpit),
            contentDescription = "QuizPit Logo",
            modifier = Modifier
                .size(200.dp)
                .sketchbookBounceIn() 
        )
        
        Spacer(modifier = Modifier.height(48.dp))

        // PIN Input
        SketchbookTextField(
            value = pin,
            onValueChange = { if (it.length <= 6) pin = it.uppercase() },
            placeholder = stringResource(id = R.string.home_game_pin_hint)
        )

        Spacer(modifier = Modifier.height(16.dp))

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

        Spacer(modifier = Modifier.height(16.dp))

        // Separador O
        Text(
            text = stringResource(id = R.string.home_or_separator),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Create Game Button
        SketchbookButton(
            onClick = { /* TODO: Lógica de crear partida */ },
            backgroundColor = MaterialTheme.colorScheme.secondary, // Rosado brillante
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
