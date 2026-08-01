import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
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

        // Inyectamos por defecto la de debug (para compilaciones en desarrollo)
        buildConfigField("String", "REVENUECAT_API_KEY", debugKey)
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

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.material3)
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
}
