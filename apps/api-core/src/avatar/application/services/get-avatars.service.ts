import { Injectable, Inject } from '@nestjs/common';
import { IGetAvatarsUseCase } from '../../domain/ports/in/get-avatars.usecase';
import { AvatarModel } from '../../domain/models/avatar.model';
import { IAvatarRepository, AVATAR_REPOSITORY } from '../../domain/ports/out/avatar.repository';

@Injectable()
export class GetAvatarsService implements IGetAvatarsUseCase {
  constructor(
    @Inject(AVATAR_REPOSITORY)
    private readonly avatarRepository: IAvatarRepository,
  ) {}

  async execute(): Promise<AvatarModel[]> {
    return this.avatarRepository.findAll();
  }
}
