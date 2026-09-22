import { Controller, Get, Inject } from '@nestjs/common';
import { IGetAvatarsUseCase } from '../../../../domain/ports/in/get-avatars.usecase';

@Controller('avatars')
export class AvatarController {
  constructor(
    @Inject('IGetAvatarsUseCase')
    private readonly getAvatarsUseCase: IGetAvatarsUseCase,
  ) {}

  @Get()
  async getAvatars() {
    return this.getAvatarsUseCase.execute();
  }
}
