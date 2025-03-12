pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    // Enable version catalogs
   // versionCatalogs {
     //   create("libs") {
       //     from(files("gradle/libs.versions.toml"))
     //   }
  //  }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Indicab"
include(":app")