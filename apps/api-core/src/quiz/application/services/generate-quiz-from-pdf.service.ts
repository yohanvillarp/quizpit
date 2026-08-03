import { Injectable, Inject, ConflictException } from '@nestjs/common';
import { AI_QUIZ_GENERATOR_PORT } from '../../domain/ports/out/ai-quiz-generator.port';
import type { IAiQuizGeneratorPort } from '../../domain/ports/out/ai-quiz-generator.port';
import { ContentModeratorService } from '../../domain/services/content-moderator.service';

@Injectable()
export class GenerateQuizFromPdfService {
  constructor(
    @Inject(AI_QUIZ_GENERATOR_PORT)
    private readonly aiQuizGenerator: IAiQuizGeneratorPort,
    private readonly contentModerator: ContentModeratorService,
  ) {}

  async execute(fileBuffer: Buffer, filename: string): Promise<any> {
    if (!fileBuffer) {
      throw new ConflictException('No se proporcionó ningún buffer de archivo PDF.');
    }
    
    try {
      return await this.aiQuizGenerator.generateFromPdfBuffer(fileBuffer, filename);
    } catch (error: any) {
      // El moderador evalúa si es un error de seguridad y lanza ForbiddenException
      this.contentModerator.handlePotentialSafetyError(error);
      
      // Si no es de seguridad, lanzamos un error general
      throw new ConflictException('Hubo un error al generar el cuestionario: ' + (error.message || 'Error desconocido'));
    }
  }
}

