rootProject.name = "mappie"

pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
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
    ":maven-plugin",
)
