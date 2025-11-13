rootProject.name = "mappie"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }

    includeBuild("build-logic")
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

include(":compiler-plugin")
include(":gradle-plugin")
include(":mappie-api")
include(":maven-plugin")
include(":modules:kotlinx-datetime")
include(":testutil")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
