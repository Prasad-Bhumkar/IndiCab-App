plugins {
    // Define plugins that can be applied to modules
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    id("com.google.dagger.hilt.android") version "2.51" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.3" apply false
    id("org.jlleitschuh.gradle.ktlint") version "11.6.1" apply false
}


dependencies {
    // Add your dependencies here
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8" // Set to a supported version
    }
}
