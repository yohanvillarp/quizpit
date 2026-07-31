FROM node:20-alpine AS base
ENV PNPM_HOME="/pnpm"
ENV PATH="$PNPM_HOME:$PATH"
RUN corepack enable

FROM base AS builder
# Set working directory
WORKDIR /app
RUN npm install -g turbo
COPY . .
# Extrae solo game-engine y sus dependencias
RUN turbo prune game-engine --docker

FROM base AS installer
WORKDIR /app
COPY --from=builder /app/out/json/ .
COPY --from=builder /app/out/pnpm-lock.yaml ./pnpm-lock.yaml
RUN pnpm install --frozen-lockfile

# Construye el proyecto
COPY --from=builder /app/out/full/ .
RUN pnpm turbo run build --filter=game-engine...

FROM base AS runner
WORKDIR /app
RUN addgroup --system --gid 1001 nodejs
RUN adduser --system --uid 1001 nestjs
USER nestjs

COPY --from=installer /app .
EXPOSE 3001
CMD node apps/game-engine/dist/main.js
