plugins {
    // Define plugins that can be applied to modules
    alias(libs.plugins.android.application) apply true
    alias(libs.plugins.kotlin.android) apply true
    id("com.google.gms.google-services") version "4.3.10" apply false
    id("com.google.dagger.hilt.android") version "2.48" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.1" apply false
}

android {
    compileSdk = 34 // Set to the latest compile SDK version
    namespace = "com.example.indicab" // Specify the namespace

    defaultConfig {
        applicationId = "com.example.indicab" // Ensure this matches the package name
        minSdk = 21 // Set minimum SDK version
        targetSdk = 34 // Set target SDK version
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    // Add your dependencies here
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8" // Set to a supported version
    }
}
