rootProject.name = "kotlin-mapping"

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
    ":processor",
    ":gradle-plugin",
    ":testing"
)
