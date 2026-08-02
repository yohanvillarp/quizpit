package tech.nikelyh.quizpit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SketchbookTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    borderColor: Color = MaterialTheme.colorScheme.onBackground,
    shadowColor: Color = MaterialTheme.colorScheme.onBackground,
    borderWidth: Dp = 4.dp,
    shadowOffset: Dp = 8.dp,
    cornerRadius: Dp = 16.dp
) {
    Box(
        modifier = modifier.padding(bottom = shadowOffset, end = shadowOffset)
    ) {
        // Solid shadow
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(x = shadowOffset, y = shadowOffset)
                .background(shadowColor, RoundedCornerShape(cornerRadius))
        )

        // Text Field Surface
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                color = textColor,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                letterSpacing = 6.sp
            ),
            cursorBrush = SolidColor(textColor),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor, RoundedCornerShape(cornerRadius))
                .border(borderWidth, borderColor, RoundedCornerShape(cornerRadius))
                .padding(vertical = 24.dp, horizontal = 24.dp),
            decorationBox = { innerTextField ->
                Box(contentAlignment = Alignment.Center) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            maxLines = 1,
                            softWrap = false,
                            style = TextStyle(
                                color = textColor.copy(alpha = 0.3f),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center,
                                letterSpacing = 6.sp
                            )
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}
