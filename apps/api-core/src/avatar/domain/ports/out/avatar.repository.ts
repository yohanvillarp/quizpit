import { AvatarModel } from '../../models/avatar.model';

export const AVATAR_REPOSITORY = Symbol('AVATAR_REPOSITORY');

export interface IAvatarRepository {
  findAll(): Promise<AvatarModel[]>;
}
