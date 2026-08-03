import { Injectable, ConflictException } from '@nestjs/common';
import type { IAiQuizGeneratorPort } from '@/quiz/domain/ports/out/ai-quiz-generator.port';
import { TextOptimizerService } from '@/quiz/domain/services/text-optimizer.service';

@Injectable()
export class GeminiQuizGeneratorAdapter implements IAiQuizGeneratorPort {
  constructor(private readonly textOptimizer: TextOptimizerService) {}

  async generateFromPdfBuffer(buffer: Buffer, filename?: string): Promise<any> {
    const apiKey = process.env.GEMINI_API_KEY;
    if (!apiKey) {
      throw new ConflictException('El servidor no tiene configurada la API KEY de Gemini.');
    }

    const { GoogleGenAI } = require('@google/genai');
    const ai = new GoogleGenAI({ apiKey });
    
    try {
      console.log(`[Adapter] Extrayendo texto del PDF con pdfjs-dist...`);
      
      // Importamos pdfjs-dist en su versión Legacy para Node.js (v3)
      const pdfjsLib = require('pdfjs-dist/legacy/build/pdf.js');
      
      // En Node.js no necesitamos el worker de navegador
      pdfjsLib.GlobalWorkerOptions.workerSrc = '';
      
      // Convertimos el Buffer de Node a Uint8Array
      const uint8Array = new Uint8Array(buffer);
      
      // Cargamos el documento
      const loadingTask = pdfjsLib.getDocument({
        data: uint8Array,
        useSystemFonts: true, // Evita dependencias nativas de Canvas en Node
      });
      const pdfDocument = await loadingTask.promise;
      
      let pdfTextRaw = '';
      for (let i = 1; i <= pdfDocument.numPages; i++) {
        const page = await pdfDocument.getPage(i);
        const textContent = await page.getTextContent();
        const pageText = textContent.items.map((item: any) => item.str).join(' ');
        pdfTextRaw += `\n\n--- PÁGINA ${i} ---\n${pageText}`;
      }

      // Optimizar texto crudo usando el servicio de dominio
      const pdfText = this.textOptimizer.optimize(pdfTextRaw);
      
      console.log(`[Adapter] Texto original: ${pdfTextRaw.length} chars. Texto optimizado: ${pdfText.length} chars. Ahorro de ${(100 - (pdfText.length/pdfTextRaw.length)*100).toFixed(1)}%. Iniciando Gemini...`);

      const model = process.env.GEMINI_MODEL || 'gemini-2.5-flash';
      let promptText = `Actúa como un experto en educación y creador de trivia.
Genera un MÁXIMO de 10 preguntas de opción múltiple basadas en este documento. Si el contenido del documento es corto o no da para tantas preguntas, genera solo las que consideres naturales (pueden ser 3, 5, etc.) sin forzar relleno.
Deberás generar un objeto JSON con la siguiente estructura exacta. NO ESCRIBAS NINGÚN TEXTO FUERA DEL JSON NI USES BLOQUES MARKDOWN:
{
  "title": "Un título académico y descriptivo",
  "description": "Una descripción educativa",
  "questions": [
    {
      "text": "La pregunta",
      "timeLimit": 20,
      "maxPoints": 100,
      "options": [
        { "text": "Respuesta correcta", "isCorrect": true },
        { "text": "Incorrecta 1", "isCorrect": false },
        { "text": "Incorrecta 2", "isCorrect": false },
        { "text": "Incorrecta 3", "isCorrect": false }
      ]
    }
  ]
}
El orden de las opciones debe ser SIEMPRE el mismo en el JSON (la correcta de primera).`;

      let contents: any = promptText;

      if (pdfText.length < 50) {
        console.log(`[Adapter] Texto insuficiente (${pdfText.length} chars). Asumiendo que es un PDF escaneado (imágenes). Activando Gemini OCR Híbrido...`);
        // Usamos el archivo en Base64 para que Gemini analice las imágenes (Visión Artificial)
        contents = [
          {
            role: 'user',
            parts: [
              { text: promptText },
              {
                inlineData: {
                  data: buffer.toString('base64'),
                  mimeType: 'application/pdf'
                }
              }
            ]
          }
        ];
      } else {
        // Modo normal (texto optimizado, ahorro masivo de tokens)
        contents = promptText + `\n\nTEXTO DEL DOCUMENTO:\n${pdfText}`;
      }

      const responseStream = await ai.models.generateContentStream({
        model: model,
        contents: contents,
        config: {
          safetySettings: [
            {
              category: 'HARM_CATEGORY_HATE_SPEECH',
              threshold: 'BLOCK_LOW_AND_ABOVE',
            },
            {
              category: 'HARM_CATEGORY_HARASSMENT',
              threshold: 'BLOCK_LOW_AND_ABOVE',
            },
            {
              category: 'HARM_CATEGORY_SEXUALLY_EXPLICIT',
              threshold: 'BLOCK_LOW_AND_ABOVE',
            },
            {
              category: 'HARM_CATEGORY_DANGEROUS_CONTENT',
              threshold: 'BLOCK_LOW_AND_ABOVE',
            }
          ]
        }
      });

      let jsonStr = "";
      console.log('--- GENERANDO (STREAMING) ---');
      for await (const chunk of responseStream) {
        const textChunk = chunk.text || "";
        process.stdout.write(textChunk);
        jsonStr += textChunk;
      }
      console.log('\n-----------------------------');

      if (jsonStr.startsWith('\`\`\`json')) {
        jsonStr = jsonStr.replace(/^\`\`\`json\n/, '').replace(/\n\`\`\`$/, '');
      } else if (jsonStr.startsWith('\`\`\`')) {
        jsonStr = jsonStr.replace(/^\`\`\`\n/, '').replace(/\n\`\`\`$/, '');
      }

      const generatedData = JSON.parse(jsonStr);
      return generatedData;
    } catch (error: any) {
      console.error('[Adapter] Error procesando PDF o con Gemini:', error);
      // Relanzamos el error original para que el Moderador pueda inspeccionarlo
      throw error;
    }
  }
}
