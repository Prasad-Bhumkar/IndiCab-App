plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
    id("dagger.hilt.android.plugin") // Hilt plugin
}

android {
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.indicab"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        kotlinOptions {
                jvmTarget = "11" // Set Kotlin JVM target to 11

        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation("com.google.dagger:hilt-android:2.44")
    kapt("com.google.dagger:hilt-compiler:2.44")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.0") // Add Kotlin standard library

    // Other dependencies...
}
