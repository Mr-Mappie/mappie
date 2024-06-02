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
    ":api",
    ":compiler-plugin",
    ":gradle-plugin",
    ":testing"
)
