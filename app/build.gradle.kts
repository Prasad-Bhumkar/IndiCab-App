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

    // AndroidX Core and UI Components
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

    // Google Maps and Location Services
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.android.libraries.places:places:3.3.0")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")

    // Coroutines for async programming
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    // Image Loading
    implementation("io.coil-kt:coil:2.5.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
