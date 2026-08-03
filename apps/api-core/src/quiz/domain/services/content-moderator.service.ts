import { Injectable, ForbiddenException } from '@nestjs/common';

@Injectable()
export class ContentModeratorService {
  /**
   * Evalúa si un error proviene de un bloqueo de seguridad de la IA
   * y lanza una excepción limpia para el cliente móvil.
   */
  handlePotentialSafetyError(error: any): void {
    const errorString = error?.toString() || '';
    const errorMessage = error?.message || '';

    // Buscamos si la IA abortó por motivos de seguridad
    // (Gemini suele incluir 'SAFETY' o 'blockReason' en su mensaje)
    const isSafetyBlock = 
      errorString.includes('SAFETY') || 
      errorMessage.includes('SAFETY') || 
      errorString.includes('HarmCategory') || 
      errorMessage.includes('HarmCategory');

    if (isSafetyBlock) {
      console.warn('[Moderator] Bloqueo de seguridad detectado. Rechazando solicitud.');
      throw new ForbiddenException(
        'El contenido de este documento viola las políticas de seguridad (Ej. violencia, lenguaje inapropiado, acoso). No puede ser procesado.'
      );
    }
  }
}
