package tech.nikelyh.quizpit.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tech.nikelyh.quizpit.ui.components.SketchyAIRobot
import tech.nikelyh.quizpit.ui.components.sketchbookBackground

import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.ui.platform.LocalContext
import tech.nikelyh.quizpit.feature.home.data.QuizRepository
import tech.nikelyh.quizpit.core.domain.file.ValidatePdfFileUseCase
import tech.nikelyh.quizpit.core.infrastructure.file.AndroidFileValidatorImpl
import android.content.Context

fun getFileName(context: Context, uri: Uri): String {
    var result: String? = null
    if (uri.scheme == "content") {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && index != -1) {
                result = cursor.getString(index)
            }
        }
    }
    if (result == null) {
        result = uri.path?.substringAfterLast('/')
    }
    return result ?: "Archivo PDF"
}

@Composable
fun GeneratingQuizOverlay(
    pdfUri: Uri?,
    onNavigateBack: () -> Unit
) {
    var dotsCount by remember { mutableIntStateOf(0) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var fileName by remember { mutableStateOf("Documento") }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val dotsJob = launch {
            while (true) {
                delay(500)
                dotsCount = (dotsCount + 1) % 4
            }
        }

        if (pdfUri != null) {
            fileName = getFileName(context, pdfUri)
            
            // 1. Instanciar el Validador (Zero Trust en Android)
            val fileValidator = AndroidFileValidatorImpl(context)
            val validatePdfUseCase = ValidatePdfFileUseCase(fileValidator)
            
            // 2. Ejecutar validación
            val validationResult = validatePdfUseCase(pdfUri)
            
            if (validationResult.isSuccess) {
                // 3. Subir si es válido
                val repository = QuizRepository(context)
                val success = repository.generateQuizFromPdf(pdfUri)
                dotsJob.cancel()
                onNavigateBack()
            } else {
                // 4. Bloquear y mostrar error si es malicioso o pesado
                dotsJob.cancel()
                errorMessage = validationResult.exceptionOrNull()?.message ?: "Error de seguridad."
                delay(3500) // Mostrar el error unos segundos antes de regresar
                onNavigateBack()
            }
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
            
            if (errorMessage != null) {
                Text(
                    text = "BLOQUEADO\n\n$errorMessage",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red, // Alerta roja
                    modifier = Modifier.width(300.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = "Generando preguntas para:\n$fileName$dots",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF1E1E1E),
                    modifier = Modifier.width(300.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
