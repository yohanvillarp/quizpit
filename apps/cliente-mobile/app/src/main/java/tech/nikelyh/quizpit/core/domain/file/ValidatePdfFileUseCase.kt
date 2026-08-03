package tech.nikelyh.quizpit.core.domain.file

import android.net.Uri

/**
 * Caso de uso responsable de aplicar todas las reglas de negocio
 * y seguridad antes de permitir la subida de un PDF.
 */
class ValidatePdfFileUseCase(
    private val fileValidator: FileValidator
) {
    suspend operator fun invoke(uri: Uri): Result<Unit> {
        return fileValidator.validatePdf(uri)
    }
}
