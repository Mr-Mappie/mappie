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
    ":mappie-kotlinx-datetime",
    ":compiler-plugin",
    ":gradle-plugin",
    ":maven-plugin",
)
