package tech.nikelyh.quizpit.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun GamePinTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    SketchbookTextField(
        value = value,
        onValueChange = { input ->
            // Only allow standard A-Z characters (no accents, no Ñ, no numbers, no symbols)
            val filtered = input.uppercase().filter { it in 'A'..'Z' }
            if (filtered.length <= 6) {
                onValueChange(filtered)
            }
        },
        placeholder = placeholder,
        modifier = modifier,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Characters,
            keyboardType = KeyboardType.Text,
            autoCorrect = false
        )
    )
}
