# QuizPit

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

QuizPit is a real-time multiplayer educational game designed to make studying active, social, and incredibly fun. Forget boring flashcards; a host uploads study material, creates a room, and students join a competitive arena where they use unique companion abilities and strategic powers to win while mastering the subject matter.

## Table of Contents
- [Core Features](#core-features)
- [Architecture](#architecture)
- [Quick Start](#quick-start)
- [License](#license)

## Core Features
- **Real-Time Multiplayer Arena:** Host study rooms and compete in real-time.
- **Companions & Abilities:** Strategic gameplay that allows students to use different powers to gain advantages or interact with opponents during the match.
- **AI Material Generation:** Turn any text or class notes into a playable quiz room instantly.
- **Cross-Platform:** Available on Web (`cliente-web`) and natively on Android (`cliente-mobile`).

## Architecture (Monorepo)
This project is a hybrid monorepo containing 5 core applications, structured for real-time scalability:

- **`apps/game-engine`**: The real-time gameplay service, powered by WebSockets and Redis.
- **`apps/api-core`**: The REST API for standard HTTP requests and AI generation.
- **`apps/cliente-web`**: The browser-based client application.
- **`apps/cliente-mobile`**: The native Android application (Multi-Module Clean Architecture).
- **`apps/studio`**: The management dashboard for stats and game rules.
- **`packages/`**: Shared libraries and utilities used across the ecosystem.

## Quick Start (Development)

1. **Clone the repository:**
   ```bash
   git clone https://github.com/yohanvillarp/quizpit.git
   cd quizpit
   ```

2. **Initialize Git Hooks:**
   ```bash
   git config core.hooksPath .husky
   ```

3. **Install Dependencies:**
   ```bash
   pnpm install
   ```

4. **Run the Android Client:**
   Open the `/apps/cliente-mobile` directory directly in Android Studio.

## License
This project is licensed under the [MIT License](LICENSE).