// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    // Define plugins that can be applied to modules
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
}

buildscript {
    // Dependencies required by the build script itself
    dependencies {
        classpath("com.google.gms:google-services:4.4.1")
    }
}


