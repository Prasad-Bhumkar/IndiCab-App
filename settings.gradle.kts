import java.net.URI
pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven { url = uri("https://www.jitpack.io") } // Example: Add JitPack if needed
        // Add any other plugin repositories you require
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://www.jitpack.io") } // Example: Add JitPack if needed
        // Add any other dependency repositories you require
    }
}

<<<<<<< HEAD
rootProject.name = "Indicab"
include(":app")
=======
rootProject.name = "IndiCab" // Replace with your project name
include(":app")
// Add other modules below, e.g.:
// include(":my-library-module")
>>>>>>> 81ec31f166cdb0573d5c5135fcdecb0f6ba49d83
