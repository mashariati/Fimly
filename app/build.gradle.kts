import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}
val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        load(FileInputStream(localPropertiesFile))
    }
}

android {
    namespace = "com.fimly.app"
    compileSdk = 35
    defaultConfig {
        applicationId = "com.fimly.app"
        minSdk = 25
        targetSdk = 37
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val tmdbToken = localProperties.getProperty("TMDB_BEARER_TOKEN") ?: ""

        buildConfigField("String", "TMDB_BEARER_TOKEN", "\"$tmdbToken\"")
        buildConfigField("String", "TMDB_BASE_URL", "\"https://api.themoviedb.org/3\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        // Source and target compatibility
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    // 1. Android Core & Lifecycle
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // 2. Compose BOM (Applies version alignment across all Compose libs)
    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))

    // 3. Compose Production Bundle
    implementation(libs.bundles.compose)

    // 4. Compose Debugging Bundle (Excluded from release builds)
    debugImplementation(libs.bundles.compose.debug)

    // 5. Unit & UI Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.bundles.compose.test)

    // Ktor for networking
    implementation(libs.bundles.ktor)

    // Koin for DI
    implementation(platform(libs.koin.bom))
    testImplementation(platform(libs.koin.bom))
    implementation(libs.bundles.koin)
    testImplementation(libs.bundles.koin.test)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Coil for image loading
    implementation(libs.bundles.coil)

    // Navigation Compose
    implementation(libs.androidx.navigation.compose)

    // Serialization
    implementation(libs.kotlinx.serialization.json)
}