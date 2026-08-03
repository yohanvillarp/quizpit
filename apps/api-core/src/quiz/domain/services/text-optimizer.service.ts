import { Injectable } from '@nestjs/common';

@Injectable()
export class TextOptimizerService {
  /**
   * Limpia y comprime el texto crudo extraído de un PDF para ahorrar tokens.
   * Elimina espacios innecesarios, múltiples saltos de línea y caracteres no imprimibles.
   */
  optimize(rawText: string): string {
    if (!rawText) return '';

    let cleanText = rawText;

    // 1. Eliminar caracteres no imprimibles (manteniendo letras, números, puntuación básica)
    // Se usa una regex genérica para quitar basura de control (ASCII 0-31, excluyendo tabulador y saltos)
    cleanText = cleanText.replace(/[\x00-\x08\x0B\x0C\x0E-\x1F\x7F]/g, '');

    // 2. Comprimir múltiples saltos de línea (3 o más) en solo 2 saltos (para mantener estructura de párrafos)
    cleanText = cleanText.replace(/\n{3,}/g, '\n\n');

    // 3. Eliminar espacios y tabulaciones dobles o más, dejándolos en un solo espacio
    cleanText = cleanText.replace(/[ \t]{2,}/g, ' ');

    // 4. Recortar espacios al inicio y final
    cleanText = cleanText.trim();

    return cleanText;
  }
}
