import { Injectable } from '@nestjs/common';
import { IAvatarRepository } from '../../../../domain/ports/out/avatar.repository';
import { AvatarModel } from '../../../../domain/models/avatar.model';
import { PrismaService } from '../../../../../infrastructure/database/prisma.service';

@Injectable()
export class PrismaAvatarRepository implements IAvatarRepository {
  constructor(private readonly prisma: PrismaService) {}

  async findAll(): Promise<AvatarModel[]> {
    const avatars = await this.prisma.avatar.findMany({
      include: {
        superPower: true,
      },
    });

    return avatars.map((avatar) => {
      return new AvatarModel(
        avatar.id,
        avatar.name,
        avatar.phrase,
        avatar.superPower?.name || '',
        avatar.superPower?.description || '',
        avatar.superPower?.effectType || '',
        (avatar.superPower as any)?.powerType || 'SPECIAL',
        avatar.isMythic,
        avatar.isUnlocked
      );
    });
  }
}
