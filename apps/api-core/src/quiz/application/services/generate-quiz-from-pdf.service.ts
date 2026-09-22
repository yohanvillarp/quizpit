import { Injectable, Inject, ConflictException } from '@nestjs/common';
import { AI_QUIZ_GENERATOR_PORT } from '../../domain/ports/out/ai-quiz-generator.port';
import type { IAiQuizGeneratorPort } from '../../domain/ports/out/ai-quiz-generator.port';
import { ContentModeratorService } from '../../domain/services/content-moderator.service';
import { QUIZ_REPOSITORY, IQuizRepository } from '../../domain/ports/out/quiz.repository';
import { GAME_ENGINE_PORT, IGameEnginePort } from '../../domain/ports/out/game-engine.port';
import { PrismaService } from '../../../infrastructure/database/prisma.service';

@Injectable()
export class GenerateQuizFromPdfService {
  constructor(
    @Inject(AI_QUIZ_GENERATOR_PORT)
    private readonly aiQuizGenerator: IAiQuizGeneratorPort,
    @Inject(QUIZ_REPOSITORY)
    private readonly quizRepository: IQuizRepository,
    @Inject(GAME_ENGINE_PORT)
    private readonly gameEnginePort: IGameEnginePort,
    private readonly contentModerator: ContentModeratorService,
    private readonly prisma: PrismaService,
  ) {}

  async execute(fileBuffer: Buffer, filename: string, hostId: string): Promise<any> {
    if (!fileBuffer) {
      throw new ConflictException('No se proporcionó ningún buffer de archivo PDF.');
    }
    
    if (!hostId) {
      throw new ConflictException('No se proporcionó el hostId (deviceId) del usuario.');
    }
    
    // Asegurar que el usuario (hostId / deviceId) existe en la base de datos para cumplir la restricción de llave foránea
    await this.prisma.user.upsert({
      where: { id: hostId },
      update: {},
      create: {
        id: hostId,
        email: `guest-${hostId}@quizpit.tech`, // Email ficticio para invitados
        name: 'Guest User',
      }
    });

    // 1. Create empty quiz in DB
    const pendingQuiz = await this.quizRepository.createQuiz({
      title: 'Generando Quiz...',
      description: 'Generando con IA',
      categoryId: 'random',
      authorId: hostId,
      questions: []
    });

    // 2. Create room in game-engine with empty quiz
    const gameEngineResponse = await this.gameEnginePort.createRoom({
      quiz: pendingQuiz,
      hostId: hostId
    });

    // 3. Fire and forget AI Generation in background
    this.generateInBackground(fileBuffer, filename, hostId, pendingQuiz.id, gameEngineResponse.roomId).catch(err => {
      console.error('Background AI generation failed:', err);
    });

    // 4. Return immediately
    return {
      quizId: pendingQuiz.id,
      roomId: gameEngineResponse.roomId,
      title: pendingQuiz.title,
      questionCount: 0
    };
  }

  private async generateInBackground(fileBuffer: Buffer, filename: string, hostId: string, quizId: string, roomId: string) {
    let generatedData;
    try {
      generatedData = await this.aiQuizGenerator.generateFromPdfBuffer(fileBuffer, filename);
    } catch (error: any) {
      // El moderador evalúa si es un error de seguridad y lanza ForbiddenException
      this.contentModerator.handlePotentialSafetyError(error);
      
      // Si no es de seguridad, lanzamos un error general
      throw new ConflictException('Hubo un error al generar el cuestionario: ' + (error.message || 'Error desconocido'));
    }

    // Usar el tema detectado por IA o uno por defecto
    const topicName = generatedData.topic || 'Categoría Aleatoria';
    const categoryId = topicName.toLowerCase().replace(/[^a-z0-9]+/g, '-');
    await this.prisma.category.upsert({
      where: { id: categoryId },
      update: {},
      create: {
        id: categoryId,
        name: topicName,
        description: 'Categoría detectada por IA'
      }
    });
    
    // Guardar en la base de datos
    // Update the pending quiz in database
    const savedQuiz = await this.quizRepository.updateQuiz(quizId, {
      title: generatedData.title || 'Quiz desde PDF',
      description: generatedData.description || 'Generado automáticamente con IA.',
      categoryId: categoryId,
      authorId: hostId,
      questions: generatedData.questions.map((q: any) => ({
        id: '', // Will be generated
        text: q.text,
        timeLimit: q.timeLimit || 20,
        maxPoints: 1000,
        options: q.options.map((o: any) => ({
          id: '',
          text: o.text,
          isCorrect: o.isCorrect
        }))
      }))
    });

    // Enviar a game-engine el quiz finalizado
    const gameEngineUrl = process.env.GAME_ENGINE_URL || 'http://localhost:3002/api';
    await fetch(`${gameEngineUrl}/rooms/${roomId}/quiz`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        questions: savedQuiz.questions
      })
    });
  }
}

