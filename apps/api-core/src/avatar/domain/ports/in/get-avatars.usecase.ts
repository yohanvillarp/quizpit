import { AvatarModel } from '../../models/avatar.model';

export interface IGetAvatarsUseCase {
  execute(): Promise<AvatarModel[]>;
}
