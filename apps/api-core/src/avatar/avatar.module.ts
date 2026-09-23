import { Module } from '@nestjs/common';
import { AvatarController } from './infrastructure/adapters/in/web/avatar.controller';
import { GetAvatarsService } from './application/services/get-avatars.service';
import { PrismaAvatarRepository } from './infrastructure/adapters/out/persistence/prisma-avatar.repository';
import { AVATAR_REPOSITORY } from './domain/ports/out/avatar.repository';
import { PrismaService } from '../infrastructure/database/prisma.service';

@Module({
  controllers: [AvatarController],
  providers: [
    PrismaService,
    {
      provide: 'IGetAvatarsUseCase',
      useClass: GetAvatarsService,
    },
    {
      provide: AVATAR_REPOSITORY,
      useClass: PrismaAvatarRepository,
    },
  ],
})
export class AvatarModule {}
