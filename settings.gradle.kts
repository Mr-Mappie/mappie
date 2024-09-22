rootProject.name = "mappie"

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        google()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
    }
}

include(
    ":mappie-api",
    ":compiler-plugin",
    ":gradle-plugin",
)
