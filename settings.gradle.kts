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
    ":modules:kotlinx-datetime",
    ":compiler-plugin",
    ":gradle-plugin",
    ":maven-plugin",
    ":testutil"
)
