import { Injectable, Logger, InternalServerErrorException } from '@nestjs/common';
import { IGameEnginePort, CreateRoomRequest, CreateRoomResponse } from '../../../../domain/ports/out/game-engine.port';

@Injectable()
export class HttpGameEngineAdapter implements IGameEnginePort {
  private readonly logger = new Logger(HttpGameEngineAdapter.name);
  private readonly gameEngineUrl = process.env.GAME_ENGINE_URL || 'http://localhost:3002/api';

  async createRoom(request: CreateRoomRequest): Promise<CreateRoomResponse> {
    try {
      const payload = {
        quizId: request.quiz.id,
        categoryId: request.quiz.categoryId,
        gameModeId: 'NORMAL', // Default for now
        visibility: 'PUBLIC',
        hostId: request.hostId,
        force: true // So if the host had an old room, it overrides
      };

      this.logger.debug(`Sending create room request to Game Engine: ${this.gameEngineUrl}/rooms`);
      
      const response = await fetch(`${this.gameEngineUrl}/rooms`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(payload),
      });

      if (!response.ok) {
        const errorText = await response.text();
        this.logger.error(`Error from Game Engine: ${response.status} - ${errorText}`);
        throw new Error(`Failed to create room: ${response.statusText}`);
      }

      const data = await response.json();
      return {
        roomId: data.roomId,
      };
    } catch (error: any) {
      this.logger.error(`Failed to connect to Game Engine: ${error.message}`);
      throw new InternalServerErrorException('No se pudo inicializar la sala de juego en el servidor.');
    }
  }
}
