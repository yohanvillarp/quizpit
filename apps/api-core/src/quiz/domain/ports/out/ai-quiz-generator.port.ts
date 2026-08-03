export const AI_QUIZ_GENERATOR_PORT = 'IAiQuizGeneratorPort';

export interface IAiQuizGeneratorPort {
  /**
   * Generates a quiz from a PDF buffer.
   * @param buffer The PDF file buffer
   * @param filename Optional filename for logging/metadata
   * @returns A promise that resolves to the generated quiz JSON object
   */
  generateFromPdfBuffer(buffer: Buffer, filename?: string): Promise<any>;
}
