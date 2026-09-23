package tech.nikelyh.quizpit.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.MenuBook
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ModeIcon(isPower: Boolean, modifier: Modifier = Modifier, tint: Color = Color(0xFF1a1f3a)) {
    Icon(
        imageVector = if (isPower) Icons.Rounded.AutoAwesome else Icons.Rounded.MenuBook,
        contentDescription = if (isPower) "Power Mode" else "Normal Mode",
        tint = tint,
        modifier = modifier.size(24.dp)
    )
}
