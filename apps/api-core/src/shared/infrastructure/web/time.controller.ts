import { Controller, Get } from '@nestjs/common';

@Controller('time')
export class TimeController {
  @Get()
  getServerTime() {
    return {
      serverTimeMs: Date.now(),
      iso: new Date().toISOString()
    };
  }
}
