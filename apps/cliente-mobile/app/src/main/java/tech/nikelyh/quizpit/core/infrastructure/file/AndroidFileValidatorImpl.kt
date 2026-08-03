package tech.nikelyh.quizpit.core.infrastructure.file

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tech.nikelyh.quizpit.core.domain.file.FileSecurityError
import tech.nikelyh.quizpit.core.domain.file.FileValidator

class AndroidFileValidatorImpl(
    private val context: Context
) : FileValidator {

    companion object {
        private const val MAX_FILE_SIZE = 5 * 1024 * 1024L // 5MB
        // "%PDF" en bytes (hex: 25 50 44 46)
        private val PDF_MAGIC_BYTES = byteArrayOf(0x25, 0x50, 0x44, 0x46)
    }

    override suspend fun validatePdf(uri: Uri): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // 1. Validar MIME Type
            val mimeType = context.contentResolver.getType(uri)
            if (mimeType != "application/pdf") {
                return@withContext Result.failure(Exception(FileSecurityError.InvalidMimeType.message))
            }

            // 2. Validar Tamaño y existencia
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                if (cursor.moveToFirst() && sizeIndex != -1) {
                    val size = cursor.getLong(sizeIndex)
                    if (size > MAX_FILE_SIZE) {
                        return@withContext Result.failure(Exception(FileSecurityError.FileTooLarge.message))
                    }
                    if (size == 0L) {
                        return@withContext Result.failure(Exception(FileSecurityError.EmptyFile.message))
                    }
                }
            } ?: return@withContext Result.failure(Exception(FileSecurityError.EmptyFile.message))

            // 3. Validar Magic Bytes (%PDF)
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val buffer = ByteArray(4)
                val bytesRead = inputStream.read(buffer, 0, 4)
                
                if (bytesRead < 4) {
                    return@withContext Result.failure(Exception(FileSecurityError.EmptyFile.message))
                }
                
                for (i in 0..3) {
                    if (buffer[i] != PDF_MAGIC_BYTES[i]) {
                        return@withContext Result.failure(Exception(FileSecurityError.InvalidMagicBytes.message))
                    }
                }
            } ?: return@withContext Result.failure(Exception(FileSecurityError.UnknownError.message))

            // Si pasa todas las pruebas
            Result.success(Unit)
            
        } catch (e: Exception) {
            Result.failure(Exception(FileSecurityError.UnknownError.message))
        }
    }
}
