rootProject.name = "mappie"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

include(
    ":mappie-api",
    ":compiler-plugin",
    ":gradle-plugin",
    ":maven-plugin",
)
