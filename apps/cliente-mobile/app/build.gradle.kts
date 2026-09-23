import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
  id("com.google.gms.google-services")
}

android {
    namespace = "tech.nikelyh.quizpit"

    buildFeatures {
        compose = true
        buildConfig = true
    }
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "tech.nikelyh.quizpit"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Leer llaves seguras desde secrets.properties
        val properties = Properties()
        val secretsFile = project.rootProject.file("secrets.properties")
        if (secretsFile.exists()) {
            properties.load(secretsFile.inputStream())
        }

        val debugKey = properties.getProperty("REVENUECAT_API_KEY_DEBUG", "\"\"")
        val releaseKey = properties.getProperty("REVENUECAT_API_KEY_RELEASE", "\"\"")

        val apiBaseUrl = properties.getProperty("API_BASE_URL", "\"http://127.0.0.1:3000/api/\"")
        val gameEngineUrl = properties.getProperty("GAME_ENGINE_URL", "\"http://127.0.0.1:3002\"")
        val googleWebClientId = properties.getProperty("GOOGLE_WEB_CLIENT_ID", "\"\"")

        // Inyectamos por defecto la de debug (para compilaciones en desarrollo)
        buildConfigField("String", "REVENUECAT_API_KEY", debugKey)
        buildConfigField("String", "API_BASE_URL", apiBaseUrl)
        buildConfigField("String", "GAME_ENGINE_URL", gameEngineUrl)
        buildConfigField("String", "GOOGLE_WEB_CLIENT_ID", googleWebClientId)
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // Sobrescribimos con la llave de producción al compilar para la tienda
            val properties = Properties()
            val secretsFile = project.rootProject.file("secrets.properties")
            if (secretsFile.exists()) {
                properties.load(secretsFile.inputStream())
            }
            val releaseKey = properties.getProperty("REVENUECAT_API_KEY_RELEASE", "\"\"")
            buildConfigField("String", "REVENUECAT_API_KEY", releaseKey)
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    // buildFeatures ya fue configurado arriba
}

// Tarea automática para ADB Reverse (Físicos)
tasks.register("adbReverse") {
    doLast {
        try {
            println("Iniciando ADB Reverse para api-core (3000) y game-engine (3002)...")
            val exec1 = Runtime.getRuntime().exec("adb reverse tcp:3000 tcp:3000")
            val exec2 = Runtime.getRuntime().exec("adb reverse tcp:3002 tcp:3002")
            exec1.waitFor()
            exec2.waitFor()
            println("ADB Reverse exitoso.")
        } catch (e: Exception) {
            println("ADB Reverse falló. Asegúrate de tener el dispositivo conectado. Error: ${e.message}")
        }
    }
}
tasks.whenTaskAdded {
    if (name.startsWith("assembleDebug") || name.startsWith("installDebug")) {
        dependsOn("adbReverse")
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
    implementation("androidx.compose.material:material-icons-extended")
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)

    implementation("com.revenuecat.purchases:purchases:10.16.0")
    implementation("com.revenuecat.purchases:purchases-ui:10.16.0")

    // Navegación
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Splash Screen
    implementation("androidx.core:core-splashscreen:1.0.1")

    // Networking (Retrofit & OkHttp)
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // WebSockets (Socket.IO)
    implementation("io.socket:socket.io-client:2.1.1")
  // Import the Firebase BoM
  implementation(platform("com.google.firebase:firebase-bom:34.17.0"))
  implementation("com.google.firebase:firebase-analytics")
  implementation("com.google.firebase:firebase-auth")
  
  // Google Sign-In with Credential Manager
  implementation("androidx.credentials:credentials:1.2.2")
  implementation("androidx.credentials:credentials-play-services-auth:1.2.2")
  implementation("com.google.android.libraries.identity.googleid:googleid:1.1.0")

  // Coil para cargar la foto de perfil real
  implementation("io.coil-kt:coil-compose:2.6.0")
}
