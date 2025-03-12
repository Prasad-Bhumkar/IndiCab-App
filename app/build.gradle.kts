// Load local.properties file for sensitive information like API keys
import java.util.Properties

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")

// Load properties if the file exists
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

plugins {
    // Apply necessary plugins for Android and Kotlin
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

android {
    // Set namespace for the application
    namespace = "com.example.indicab"

    // Compile SDK version
    compileSdk = 34

    defaultConfig {
        // Use MAPS_API_KEY from local.properties if available
        manifestPlaceholders["MAPS_API_KEY"] = localProperties.getProperty("MAPS_API_KEY") ?: ""

        // Application ID
        applicationId = "com.example.indicab"

        // Minimum and target SDK versions
        minSdk = 24
        targetSdk = 34

        // Version code and name
        versionCode = 1
        versionName = "1.0"

        // Test instrumentation runner
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            // Disable minification for now
            isMinifyEnabled = false

            // ProGuard configuration
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // Compile options for Java compatibility
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    // Kotlin options to match Java version
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    // Material design components
    implementation("com.google.android.material:material:1.11.0")

    // Core KTX for Kotlin extensions
    implementation("androidx.core:core-ktx:1.13.0")

    // AppCompat for backward compatibility
    implementation("androidx.appcompat:appcompat:1.7.0")

    // Google Places API
    implementation("com.google.android.libraries.places:places:3.4.0")

    // Unit testing
    testImplementation("junit:junit:4.13.2")

    // Instrumented tests
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
