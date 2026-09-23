import { QuizModel } from '../../models/quiz.model';

export const GAME_ENGINE_PORT = Symbol('GAME_ENGINE_PORT');

export interface CreateRoomRequest {
  quiz: QuizModel;
  hostId: string;
}

export interface CreateRoomResponse {
  roomId: string;
}

export interface IGameEnginePort {
  createRoom(request: CreateRoomRequest): Promise<CreateRoomResponse>;
}
