pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS) // keep this
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "hiking"
include(":app")
