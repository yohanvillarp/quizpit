package tech.nikelyh.quizpit.core.domain.file

import android.net.Uri

/**
 * Errores posibles durante la validación de un archivo.
 */
sealed class FileSecurityError(val message: String) {
    object FileTooLarge : FileSecurityError("El archivo excede el tamaño máximo permitido (5MB).")
    object InvalidMimeType : FileSecurityError("El formato del archivo no es válido. Debe ser PDF.")
    object InvalidMagicBytes : FileSecurityError("Firma digital inválida. El archivo está corrupto o camuflado.")
    object EmptyFile : FileSecurityError("El archivo está vacío o no se pudo leer.")
    object UnknownError : FileSecurityError("Error desconocido al procesar el archivo.")
}

/**
 * Puerto (Interfaz) para validar archivos antes de subir.
 * Esto asegura que el caso de uso no dependa de librerías nativas de Android.
 */
interface FileValidator {
    suspend fun validatePdf(uri: Uri): Result<Unit>
}
