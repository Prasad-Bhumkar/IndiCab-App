// Load local.properties file for sensitive information like API keys
import java.util.Properties

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")

// Enforce existence of local.properties
if (!localPropertiesFile.exists()) {
    throw GradleException("local.properties file is missing. Please create it with the required configuration.")
}

localProperties.load(localPropertiesFile.inputStream())

plugins {
    // Apply necessary plugins for Android and Kotlin
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
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
            isMinifyEnabled = true
            isShrinkResources = true
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
    // Hilt dependencies
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.hilt.navigation.compose)

    // Material design components
    implementation(libs.material)

    // AndroidX Core and UI Components
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)

    // Google Maps and Location Services
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.places)

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-analytics")
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.perf)

    // Coroutines for async programming
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.play.services)
    
    // Networking
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.okhttp.tls)

    // Image Loading
    implementation(libs.coil.compose)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.mockk)
    testImplementation(libs.truth)
    testImplementation(libs.coroutines.test)
    
    androidTestImplementation(libs.androidx.junit.v121)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.5.4")
    androidTestImplementation("androidx.compose.ui:ui-test-manifest:1.5.4")
    androidTestImplementation("androidx.navigation:navigation-testing:2.7.5")
    androidTestImplementation("io.mockk:mockk-android:1.13.8")
}
