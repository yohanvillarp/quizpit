package tech.nikelyh.quizpit.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.unit.dp

@Composable
fun SketchyAIRobot(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "robot_anim")
    
    // Stop-motion jitter
    val jitter by infiniteTransition.animateFloat(
        initialValue = -1.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(120, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "robot_jitter"
    )

    // Hover up and down
    val hoverY by infiniteTransition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "robot_hover"
    )

    // Lightbulb pulse
    val bulbPulse by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "bulb_pulse"
    )

    // Floating symbols (question marks)
    val symbolY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -60f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "symbol_y"
    )

    Canvas(modifier = modifier.size(320.dp)) {
        val cx = size.width / 2
        val cy = size.height / 2
        
        // Convert DP to PX for density-independent drawing!
        val scale = size.width / 320f // Base scale factor assuming 320dp canvas width
        
        fun dp(value: Float): Float = value * density
        
        val inkColor = Color(0xFF1E1E1E)
        val primaryColor = Color(0xFFE57373)
        val highlightColor = Color(0xFFFFCC80)
        
        val thickStroke = Stroke(width = dp(6f), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val normalStroke = Stroke(width = dp(4f), cap = StrokeCap.Round, join = StrokeJoin.Round)
        val thinStroke = Stroke(width = dp(2f), cap = StrokeCap.Round, join = StrokeJoin.Round)

        // Apply Hover and Jitter to the whole Canvas
        translate(top = dp(hoverY)) {
            rotate(degrees = jitter, pivot = Offset(cx, cy)) {
                // --- LIGHTBULB GLOW ---
                val bulbRadius = dp(50f)
                val bulbCenterY = cy - dp(90f)
                
                drawCircle(
                    color = highlightColor.copy(alpha = bulbPulse * 0.4f),
                    radius = bulbRadius + dp(bulbPulse * 20f),
                    center = Offset(cx, bulbCenterY)
                )

                // --- FLOATING QUESTION MARKS ---
                val symbolAlpha = 1f - (-symbolY / 60f)
                // Left Question Mark
                drawPath(
                    path = Path().apply {
                        val qx = cx - dp(90f)
                        val qy = cy - dp(50f) + dp(symbolY)
                        moveTo(qx - dp(10f), qy)
                        cubicTo(qx - dp(10f), qy - dp(15f), qx + dp(10f), qy - dp(15f), qx + dp(10f), qy - dp(5f))
                        quadraticBezierTo(qx + dp(10f), qy + dp(5f), qx, qy + dp(10f))
                        lineTo(qx, qy + dp(15f))
                    },
                    color = inkColor.copy(alpha = symbolAlpha),
                    style = normalStroke
                )
                drawCircle(
                    color = inkColor.copy(alpha = symbolAlpha),
                    radius = dp(2f),
                    center = Offset(cx - dp(90f), cy - dp(30f) + dp(symbolY))
                )

                // Right Question Mark
                drawPath(
                    path = Path().apply {
                        val qx = cx + dp(90f)
                        val qy = cy - dp(70f) + dp(symbolY * 1.2f)
                        moveTo(qx - dp(10f), qy)
                        cubicTo(qx - dp(10f), qy - dp(15f), qx + dp(10f), qy - dp(15f), qx + dp(10f), qy - dp(5f))
                        quadraticBezierTo(qx + dp(10f), qy + dp(5f), qx, qy + dp(10f))
                        lineTo(qx, qy + dp(15f))
                    },
                    color = inkColor.copy(alpha = symbolAlpha),
                    style = normalStroke
                )
                drawCircle(
                    color = inkColor.copy(alpha = symbolAlpha),
                    radius = dp(2f),
                    center = Offset(cx + dp(90f), cy - dp(50f) + dp(symbolY * 1.2f))
                )

                // --- LIGHTBULB ---
                val bulbPath = Path().apply {
                    moveTo(cx - dp(35f), bulbCenterY)
                    cubicTo(cx - dp(35f), bulbCenterY - dp(45f), cx + dp(35f), bulbCenterY - dp(45f), cx + dp(35f), bulbCenterY)
                    quadraticBezierTo(cx + dp(35f), bulbCenterY + dp(30f), cx + dp(15f), bulbCenterY + dp(40f))
                    lineTo(cx - dp(15f), bulbCenterY + dp(40f))
                    quadraticBezierTo(cx - dp(35f), bulbCenterY + dp(30f), cx - dp(35f), bulbCenterY)
                }
                drawPath(path = bulbPath, color = highlightColor.copy(alpha = 0.9f))
                drawPath(path = bulbPath, color = inkColor, style = thickStroke)

                // Filament
                val filPath = Path().apply {
                    moveTo(cx - dp(10f), bulbCenterY + dp(40f))
                    lineTo(cx - dp(15f), bulbCenterY - dp(10f))
                    lineTo(cx, bulbCenterY - dp(25f))
                    lineTo(cx + dp(15f), bulbCenterY - dp(10f))
                    lineTo(cx + dp(10f), bulbCenterY + dp(40f))
                }
                drawPath(path = filPath, color = inkColor, style = normalStroke)
                drawCircle(color = highlightColor, radius = dp(8f * bulbPulse), center = Offset(cx, bulbCenterY - dp(25f)))

                // Base of Lightbulb
                drawRoundRect(
                    color = inkColor,
                    topLeft = Offset(cx - dp(18f), bulbCenterY + dp(40f)),
                    size = Size(dp(36f), dp(15f)),
                    cornerRadius = CornerRadius(dp(4f), dp(4f))
                )
                drawLine(
                    color = inkColor,
                    start = Offset(cx, bulbCenterY + dp(55f)),
                    end = Offset(cx, cy - dp(20f)),
                    strokeWidth = dp(8f),
                    pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(dp(8f), dp(8f)), 0f)
                )

                // --- ROBOT HEAD ---
                val headWidth = dp(160f)
                val headHeight = dp(120f)
                val headTopLeft = Offset(cx - headWidth / 2, cy - dp(30f))
                
                // Ears
                drawRoundRect(
                    color = inkColor,
                    topLeft = Offset(cx - headWidth / 2 - dp(15f), cy + dp(10f)),
                    size = Size(dp(15f), dp(40f)),
                    cornerRadius = CornerRadius(dp(8f), dp(8f))
                )
                drawRoundRect(
                    color = inkColor,
                    topLeft = Offset(cx + headWidth / 2, cy + dp(10f)),
                    size = Size(dp(15f), dp(40f)),
                    cornerRadius = CornerRadius(dp(8f), dp(8f))
                )

                // Main Head Box
                drawRoundRect(
                    color = Color.White,
                    topLeft = headTopLeft,
                    size = Size(headWidth, headHeight),
                    cornerRadius = CornerRadius(dp(20f), dp(20f))
                )
                drawRoundRect(
                    color = inkColor,
                    topLeft = headTopLeft,
                    size = Size(headWidth, headHeight),
                    cornerRadius = CornerRadius(dp(20f), dp(20f)),
                    style = thickStroke
                )

                // Eyes
                val eyeY = cy + dp(20f)
                val eyeDist = dp(35f)
                
                // Left Eye (Looking up/thinking)
                drawLine(
                    color = inkColor,
                    start = Offset(cx - eyeDist - dp(15f), eyeY + dp(5f)),
                    end = Offset(cx - eyeDist + dp(5f), eyeY - dp(15f)),
                    strokeWidth = dp(10f),
                    cap = StrokeCap.Round
                )
                // Right Eye
                drawLine(
                    color = inkColor,
                    start = Offset(cx + eyeDist - dp(5f), eyeY - dp(15f)),
                    end = Offset(cx + eyeDist + dp(15f), eyeY + dp(5f)),
                    strokeWidth = dp(10f),
                    cap = StrokeCap.Round
                )

                // Blush
                drawCircle(color = primaryColor.copy(alpha = 0.4f), radius = dp(12f), center = Offset(cx - eyeDist - dp(15f), eyeY + dp(25f)))
                drawCircle(color = primaryColor.copy(alpha = 0.4f), radius = dp(12f), center = Offset(cx + eyeDist + dp(15f), eyeY + dp(25f)))

                // Mouth
                val mouthPath = Path().apply {
                    moveTo(cx - dp(15f), cy + dp(45f))
                    quadraticBezierTo(cx, cy + dp(55f), cx + dp(15f), cy + dp(40f))
                }
                drawPath(path = mouthPath, color = inkColor, style = normalStroke)

                // EUREKA LINES
                if (bulbPulse > 0.8f) {
                    val lineLen = dp(20f)
                    val dist = dp(60f)
                    drawLine(
                        color = primaryColor,
                        start = Offset(cx - dist, bulbCenterY - dist),
                        end = Offset(cx - dist - lineLen, bulbCenterY - dist - lineLen),
                        strokeWidth = dp(6f),
                        cap = StrokeCap.Round
                    )
                    drawLine(
                        color = primaryColor,
                        start = Offset(cx + dist, bulbCenterY - dist),
                        end = Offset(cx + dist + lineLen, bulbCenterY - dist - lineLen),
                        strokeWidth = dp(6f),
                        cap = StrokeCap.Round
                    )
                }
            }
        }
    }
}
