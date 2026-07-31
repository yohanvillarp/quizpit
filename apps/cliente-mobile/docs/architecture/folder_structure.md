# Arquitectura Multi-Módulo para Android (Nativo)

Dado que el proyecto está enfocado exclusivamente en **Android nativo** y diseñado para ser **altamente escalable**, implementaremos una **Arquitectura Multi-Módulo por Features**. 

Esta separación mejora significativamente los tiempos de compilación, fomenta la reutilización, evita el acoplamiento y facilita el trabajo en equipo. Además, integra Clean Architecture (Dominio, Datos, Presentación) dentro de cada módulo funcional.

## Estructura de Directorios

```text
QuizPitAndroid/
│
├── app/                                 [Módulo principal - Ensambla la aplicación]
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       └── kotlin/com/nikelyh/quizpit/
│           ├── MainActivity.kt
│           ├── QuizPitApp.kt              [Clase Application]
│           └── navigation/                [Grafo de navegación principal]
│
├── core/                                [Módulos transversales (Cimientos)]
│   ├── network/                         [Configuración de red]
│   │   ├── build.gradle.kts
│   │   └── src/main/kotlin/.../core/network/
│   │       ├── ApiClient.kt             [Retrofit o Ktor config]
│   │       ├── Interceptors.kt
│   │       └── ErrorHandling.kt
│   │
│   ├── database/                        [Configuración de base de datos local]
│   │   ├── build.gradle.kts
│   │   └── src/main/kotlin/.../core/database/
│   │       ├── AppDatabase.kt           [Room Database]
│   │       └── dao/                     [DAOs compartidos o base]
│   │
│   ├── ai/                              [Módulo para integración con IA (Cloud)]
│   │   ├── build.gradle.kts
│   │   └── src/main/kotlin/.../core/ai/
│   │       ├── AiClient.kt              [Cliente para Vertex AI / OpenAI / etc.]
│   │       ├── PromptTemplates.kt       [Plantillas para generación de preguntas]
│   │       └── TokenManager.kt          [Gestión y optimización de tokens]
│   │
│   ├── designsystem/                    [Sistema de diseño (UI)]
│   │   ├── build.gradle.kts
│   │   └── src/main/kotlin/.../core/designsystem/
│   │       ├── theme/                   [Tema de Compose]
│   │       │   ├── Color.kt
│   │       │   ├── Theme.kt
│   │       │   └── Typography.kt
│   │       └── component/               [Componentes UI reutilizables]
│   │           ├── PrimaryButton.kt
│   │           ├── LoadingView.kt
│   │           └── CustomTextField.kt
│   │
│   └── common/                          [Utilidades comunes y base]
│       ├── build.gradle.kts
│       └── src/main/kotlin/.../core/common/
│           ├── exception/               [Excepciones base]
│           ├── result/                  [Clases envoltorio como Result o Resource]
│           └── extension/               [Extensiones de Kotlin (Strings, Dates)]
│
├── feature/                             [Módulos de funcionalidades (Features verticales)]
│   │
│   ├── auth/                            [Feature: Autenticación]
│   │   ├── build.gradle.kts
│   │   └── src/main/kotlin/.../feature/auth/
│   │       ├── domain/                  [Reglas de negocio]
│   │       │   ├── model/               [User, Session]
│   │       │   ├── repository/          [IAuthRepository]
│   │       │   └── usecase/             [LoginUseCase, SignUpUseCase]
│   │       ├── data/                    [Datos y APIs]
│   │       │   ├── repository/          [AuthRepositoryImpl]
│   │       │   └── remote/              [AuthService, DTOs]
│   │       └── presentation/            [UI en Compose]
│   │           ├── LoginViewModel.kt
│   │           ├── LoginScreen.kt
│   │           └── navigation/          [Grafo interno del feature]
│   │
│   ├── quiz_generation/                 [Feature: Generación de Quizzes con IA]
│   │   ├── build.gradle.kts
│   │   └── src/main/kotlin/.../feature/quiz_generation/
│   │       ├── domain/
│   │       │   ├── model/               [GeneratedQuiz, TopicRequest]
│   │       │   ├── repository/
│   │       │   └── usecase/             [GenerateQuizFromTextUseCase]
│   │       ├── data/
│   │       │   ├── repository/
│   │       │   └── remote/              [Consumo del módulo 'core:ai']
│   │       └── presentation/
│   │           ├── PromptViewModel.kt
│   │           ├── PromptScreen.kt      [Pantalla donde el usuario ingresa el texto]
│   │           └── GenerationResultScreen.kt
│   │
│   └── quiz_play/                       [Feature: Ejecución de Quizzes]
│       ├── build.gradle.kts
│       └── src/main/kotlin/.../feature/quiz_play/
│           ├── domain/
│           ├── data/
│           └── presentation/
│               ├── PlayQuizViewModel.kt
│               └── PlayQuizScreen.kt
│
├── build-logic/                         [OPCIONAL pero recomendado para escalar]
│   └── convention/                      [Plugins de convención de Gradle centralizados]
│       ├── build.gradle.kts
│       └── src/main/kotlin/
│           ├── AndroidApplicationConventionPlugin.kt
│           ├── AndroidFeatureConventionPlugin.kt
│           └── AndroidLibraryComposeConventionPlugin.kt
│
├── gradle/
│   └── libs.versions.toml               [Gestión centralizada de dependencias (Version Catalogs)]
├── build.gradle.kts                     [Build script a nivel de proyecto]
└── settings.gradle.kts                  [Declaración de todos los módulos (:app, :core:network, etc.)]
```

## Beneficios de este enfoque:

1. **Velocidad de compilación**: Al modificar una pantalla en `feature:auth`, Gradle solo recompila ese módulo y `app`, pero no `feature:quiz_generation` ni `core:network`.
2. **Escalabilidad del equipo**: Diferentes desarrolladores pueden trabajar en diferentes "features" sin causar conflictos constantes de "merge".
3. **Módulo de IA aislado**: Al tener `core:ai`, centralizamos la lógica de consumo de tokens y configuración de la nube (ej. Gemini, OpenAI) para que cualquier feature (`quiz_generation`, u otros en el futuro) pueda usarlo fácilmente.
4. **Clean Architecture Vertical**: Cada feature es dueño de su Dominio, Datos y Presentación. No hay un "god module" de datos.
5. **Version Catalogs (`libs.versions.toml`)**: Ideal para mantener las versiones de dependencias (Compose, Room, Koin/Hilt, etc.) sincronizadas en todos los módulos.
