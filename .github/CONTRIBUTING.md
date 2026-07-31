# Contributing to QuizPit

First off, thank you for considering contributing to QuizPit! 

## Code of Conduct
By participating in this project, you are expected to uphold our [Code of Conduct](CODE_OF_CONDUCT.md).

## Branching Strategy (Git Flow)
- **`main`**: Stable production branch.
- **`develop`**: Integration branch. **All Pull Requests must point to `develop`.**

## Commit Convention
This repository strictly enforces [Conventional Commits](https://www.conventionalcommits.org/). 
Please read our [Agent Rules](../.agents/AGENTS.md) for internal rules. We use Husky to validate commit messages automatically.

## Monorepo Structure
- `apps/cliente-mobile`: Native Android app (Kotlin).
- `apps/backend-api`: Node.js backend services.
- `packages/`: Shared packages and libraries.

Run `pnpm install` at the root of the repository to install dependencies for the web/backend ecosystem.
