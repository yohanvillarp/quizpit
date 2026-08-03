import { Module } from '@nestjs/common';
import { QuizController } from './infrastructure/adapters/in/web/quiz.controller.js';
import { CreateQuizService } from './application/services/create-quiz.service.js';
import { PrismaQuizRepository } from './infrastructure/adapters/out/persistence/prisma-quiz.repository.js';
import { QUIZ_REPOSITORY } from './domain/ports/out/quiz.repository.js';
import { PrismaService } from '../infrastructure/database/prisma.service.js';

import { GenerateQuizFromPdfService } from './application/services/generate-quiz-from-pdf.service.js';
import { GeminiQuizGeneratorAdapter } from './infrastructure/adapters/out/ai/gemini-quiz-generator.adapter.js';
import { AI_QUIZ_GENERATOR_PORT } from './domain/ports/out/ai-quiz-generator.port.js';
import { TextOptimizerService } from './domain/services/text-optimizer.service.js';
import { ContentModeratorService } from './domain/services/content-moderator.service.js';

@Module({
  controllers: [QuizController],
  providers: [
    PrismaService, // Usualmente estaría en un DatabaseModule compartido
    CreateQuizService,
    GenerateQuizFromPdfService,
    TextOptimizerService,
    ContentModeratorService,
    {
      provide: QUIZ_REPOSITORY,
      useClass: PrismaQuizRepository, // Inyección de Dependencias Hexagonal
    },
    {
      provide: AI_QUIZ_GENERATOR_PORT,
      useClass: GeminiQuizGeneratorAdapter,
    },
  ],
})
export class QuizModule {}
