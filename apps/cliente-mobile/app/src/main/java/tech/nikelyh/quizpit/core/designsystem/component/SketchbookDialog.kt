package tech.nikelyh.quizpit.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import tech.nikelyh.quizpit.R
import tech.nikelyh.quizpit.ui.components.SketchbookButton

private val Navy = Color(0xFF1a1f3a)
private val Paper = Color(0xFFFAF9F5)
private val LightPink = Color(0xFFFFD1DC)

@Composable
fun SketchbookDialog(
    title: String,
    message: String,
    confirmText: String = "CONFIRMAR",
    cancelText: String = "CANCELAR",
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    onDismissRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    // Analog Sketchbook aesthetic: thick borders, paper background
                    .background(Paper, RoundedCornerShape(12.dp))
                    .border(4.dp, Navy, RoundedCornerShape(12.dp))
            ) {
                // Header (Pink background like the image)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(LightPink, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground), // Replace with warning icon if available
                        contentDescription = "Warning",
                        tint = Navy,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title.uppercase(),
                        fontWeight = FontWeight.Black,
                        color = Navy,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                
                Divider(color = Navy, thickness = 4.dp)
                
                // Body
                Text(
                    text = message,
                    modifier = Modifier.padding(16.dp),
                    color = Navy,
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.bodyLarge
                )
                
                Divider(color = Navy, thickness = 4.dp)
                
                // Footer buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    SketchbookButton(
                        onClick = onCancel,
                        modifier = Modifier.weight(1f),
                        backgroundColor = Paper,
                        contentColor = Navy
                    ) {
                        Text(text = cancelText, fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    SketchbookButton(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        backgroundColor = Paper,
                        contentColor = Navy
                    ) {
                        Text(text = confirmText, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
