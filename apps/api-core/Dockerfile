FROM node:20-alpine AS base
ENV PNPM_HOME="/pnpm"
ENV PATH="$PNPM_HOME:$PATH"
RUN corepack enable

FROM base AS builder
# Set working directory
WORKDIR /app
RUN npm install -g turbo
COPY . .
# Extrae solo api-core y sus dependencias compartidas
RUN turbo prune api-core --docker

FROM base AS installer
WORKDIR /app
# Instala dependencias usando el lockfile
COPY --from=builder /app/out/json/ .
COPY --from=builder /app/out/pnpm-lock.yaml ./pnpm-lock.yaml
RUN pnpm install --frozen-lockfile

# Construye el proyecto
COPY --from=builder /app/out/full/ .
RUN pnpm turbo run build --filter=api-core...

FROM base AS runner
WORKDIR /app
# Buenas prácticas de seguridad: usar usuario sin privilegios root
RUN addgroup --system --gid 1001 nodejs
RUN adduser --system --uid 1001 nestjs
USER nestjs

COPY --from=installer /app .
EXPOSE 3000
CMD node apps/api-core/dist/main.js
