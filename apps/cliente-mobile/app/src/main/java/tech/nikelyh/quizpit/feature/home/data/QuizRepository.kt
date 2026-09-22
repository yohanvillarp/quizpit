package tech.nikelyh.quizpit.feature.home.data

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import tech.nikelyh.quizpit.core.network.RetrofitClient
import java.io.File
import java.io.FileOutputStream

class QuizRepository(private val context: Context) {
    private val apiService = RetrofitClient.apiService

    suspend fun generateQuizFromPdf(pdfUri: Uri, deviceId: String): GeneratedQuizResult? {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Copy the Uri content to a temporary file
                val tempFile = File(context.cacheDir, "temp_upload.pdf")
                val inputStream = context.contentResolver.openInputStream(pdfUri)
                val outputStream = FileOutputStream(tempFile)
                inputStream?.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }

                // 2. Create MultipartBody
                val requestFile = tempFile.asRequestBody("application/pdf".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", tempFile.name, requestFile)
                
                // 3. Create hostId part
                val hostIdBody = okhttp3.RequestBody.create("text/plain".toMediaTypeOrNull(), deviceId)

                // 4. Send to API
                val response = apiService.generateFromPdf(body, hostIdBody)
                
                // 5. Clean up
                tempFile.delete()

                if (response.isSuccessful) {
                    response.body()
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
