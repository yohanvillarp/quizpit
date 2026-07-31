# Reglas Globales para Agentes de IA en QuizPit

Estás trabajando en el monorepo de QuizPit. Para mantener la consistencia y la calidad del código, DEBES adherirte estrictamente a las siguientes reglas:

## 1. Estrategia de Ramas (Git Flow)
- **`develop`**: Es la rama de integración principal. TODO el trabajo (nuevas características, correcciones de errores, refactorizaciones) DEBE nacer de `develop`. Los Pull Requests siempre deben apuntar a `develop`.
- **`main`**: Es la rama de producción. NUNCA propongas cambios directos hacia `main`. `main` solo se actualiza fusionando código probado desde `develop`.

## 2. Convenciones de GitHub y Commits
- TODOS los commits y títulos de Pull Requests DEBEN seguir la convención de [Conventional Commits](https://www.conventionalcommits.org/).
- Esta convención está aplicada automáticamente en el repositorio a través de Husky y Commitlint.
- Formato: `<tipo>[ámbito opcional]: <descripción>`
- Tipos permitidos: `feat`, `fix`, `chore`, `docs`, `refactor`, `test`, `build`.

## 3. Arquitectura del Monorepo Híbrido
El ecosistema está dividido en 5 aplicaciones core y paquetes compartidos:
- **`apps/game-engine`**: Motor del juego en tiempo real (WebSockets, Redis). Ecosistema Node.js (pnpm).
- **`apps/api-core`**: API REST para solicitudes asíncronas y Auth. Ecosistema Node.js (pnpm).
- **`apps/cliente-web`**: Cliente web para navegadores. Ecosistema Node.js (pnpm).
- **`apps/cliente-mobile`**: Cliente nativo Android en **Kotlin** con arquitectura Multi-Módulo (Clean Architecture). Usa Gradle, no pnpm.
- **`apps/studio`**: Panel de control y gestión de estadísticas. Ecosistema Node.js (pnpm).
- **`packages/`**: Código compartido. Solo aplicable al ecosistema web/backend (TypeScript/Node) gestionado por pnpm.

## 4. Prácticas de Código
- Prioriza escribir código modular y reutilizable.
- Utiliza siempre rutas absolutas o alias si están configurados en el proyecto.
- Mantén el idioma consistente (ej. si el código fuente de Android está en inglés, respétalo).

## 5. Tono y Documentación
- **Cero Emojis**: Mantén la documentación (READMEs, manuales, código) con un tono corporativo, profesional y limpio. Evita el uso de emojis en archivos oficiales del proyecto.
- **Formato Claro**: Usa insignias (badges), índices (table of contents) y encabezados limpios para estructurar la información.

## 6. Cumplimiento Estricto de Reglas
- **Sin atajos**: NINGÚN agente de IA está autorizado a saltarse las reglas del repositorio, lineamientos de código o convenciones arquitectónicas bajo la excusa de "ahorrar tiempo" o simplificar una tarea.
- **Autorización explícita**: Si por alguna razón técnica o estructural es imposible cumplir una regla, el agente DEBE detenerse, justificar detalladamente el motivo y solicitar autorización explícita al usuario antes de proceder.

## 7. Arquitecturas Obligatorias por Aplicación
Para mantener una alta cohesión y bajo acoplamiento, los agentes deben apegarse estrictamente a la arquitectura de cada aplicación. **NUNCA** debes mezclar patrones ni saltarte las capas predefinidas:

- **`api-core` y `game-engine` (Backend)**: Usan **Arquitectura Hexagonal (Clean Architecture)**. TODO código nuevo debe separarse estrictamente en capas: `domain` (reglas puras, sin dependencias), `application` (orquestación/casos de uso) e `infrastructure` (controladores web, TypeORM/Prisma, sockets). Las importaciones deben usar alias absolutos (ej. `@quiz/domain/...`).
- **`studio` (Frontend React)**: Usa **Feature-Sliced Design (FSD)**. Cada nuevo elemento visual o lógico debe colocarse correctamente en su capa: `app`, `pages`, `widgets`, `features`, `entities`, o `shared`.
- **`cliente-mobile` (Android)**: Usa **Arquitectura Multi-Módulo**. Separación rígida entre `core` (lógica) y `feature` (UI Jetpack Compose).
