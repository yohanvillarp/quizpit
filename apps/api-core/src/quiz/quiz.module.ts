import { Module } from '@nestjs/common';
import { QuizController } from './infrastructure/adapters/in/web/quiz.controller';
import { CreateQuizService } from './application/services/create-quiz.service';
import { PrismaQuizRepository } from './infrastructure/adapters/out/persistence/prisma-quiz.repository';
import { QUIZ_REPOSITORY } from './domain/ports/out/quiz.repository';
import { PrismaService } from '../infrastructure/database/prisma.service';

import { GenerateQuizFromPdfService } from './application/services/generate-quiz-from-pdf.service';
import { GeminiQuizGeneratorAdapter } from './infrastructure/adapters/out/ai/gemini-quiz-generator.adapter';
import { AI_QUIZ_GENERATOR_PORT } from './domain/ports/out/ai-quiz-generator.port';
import { TextOptimizerService } from './domain/services/text-optimizer.service';
import { ContentModeratorService } from './domain/services/content-moderator.service';
import { GAME_ENGINE_PORT } from './domain/ports/out/game-engine.port';
import { HttpGameEngineAdapter } from './infrastructure/adapters/out/game-engine/http-game-engine.adapter';

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
    {
      provide: GAME_ENGINE_PORT,
      useClass: HttpGameEngineAdapter,
    },
  ],
})
export class QuizModule {}

