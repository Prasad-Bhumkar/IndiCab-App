plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.indicab"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.indicab"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        kotlinOptions {
            jvmTarget = "17" // Set Kotlin JVM target to 17
        }
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
}

dependencies {
    // Hilt dependencies
    implementation("com.google.dagger:hilt-android:2.55")
    kapt("com.google.dagger:hilt-android-compiler:2.55")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Material design components
    implementation("com.google.android.material:material:1.11.0")

    // AndroidX Core and UI Components
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.constraintlayout:constraintlayout:2.2.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.9")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.9")

    // Google Maps and Location Services
    implementation("com.google.android.gms:play-services-maps:19.2.0")
    implementation("com.google.android.gms:play-services-location:21.3.1") {
        exclude(group = "org.bouncycastle", module = "bcprov-jdk18on")
    }
    implementation("com.google.android.libraries.places:places:4.1.0")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.11.0")) {
        exclude(group = "org.bouncycastle", module = "bcprov-jdk18on")
    }
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage") {
        exclude(group = "org.bouncycastle", module = "bcprov-jdk18on")
    }
    implementation("com.google.firebase:firebase-crashlytics") {
        exclude(group = "org.bouncycastle", module = "bcprov-jdk18on")
    }
    implementation("com.google.firebase:firebase-perf") {
        exclude(group = "org.bouncycastle", module = "bcprov-jdk18on")
    }
    implementation("com.google.firebase:firebase-messaging")

    // Coroutines for async programming
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.1")

    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.11.1")
    implementation("com.squareup.retrofit2:converter-gson:2.11.1")
    implementation("com.squareup.okhttp3:okhttp:4.12.1") {
        exclude(group = "org.bouncycastle", module = "bcprov-jdk18on")
    }
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.okhttp3:okhttp-tls:4.12.0")

    // Image Loading
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.7.8")
    androidTestImplementation("androidx.compose.ui:ui-test-manifest:1.7.8")
    androidTestImplementation("androidx.navigation:navigation-testing:2.8.9")
    androidTestImplementation("io.mockk:mockk-android:1.13.17")
}
