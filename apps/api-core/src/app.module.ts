import { Module } from '@nestjs/common';
import { ConfigModule } from '@nestjs/config';
import { QuizModule } from './quiz/quiz.module';
import { LoggerModule } from 'nestjs-pino';
import { TimeController } from './shared/infrastructure/web/time.controller';
import { AvatarModule } from './avatar/avatar.module';

@Module({
  imports: [
    ConfigModule.forRoot({
      isGlobal: true,
      envFilePath: ['.env', '.env.local'],
    }),
    LoggerModule.forRoot({
      pinoHttp: {
        level: process.env.NODE_ENV !== 'production' ? 'debug' : 'info',
        transport:
          process.env.NODE_ENV !== 'production'
            ? { target: 'pino-pretty' }
            : undefined,
      },
    }),
    QuizModule,
    AvatarModule,
  ],
  controllers: [TimeController],
  providers: [],
})
export class AppModule {}
