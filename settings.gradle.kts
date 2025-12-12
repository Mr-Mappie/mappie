rootProject.name = "mappie"

pluginManagement {
    repositories {
        gradlePluginPortal()
    }

    includeBuild("build-logic")
}

plugins {
    id("org.gradle.toolchains.foojay-resolver") version "1.0.0"
}

toolchainManagement {
    jvm {
        javaRepositories {
            repository("foojay") {
                resolverClass = org.gradle.toolchains.foojay.FoojayToolchainResolver::class.java
            }
        }
    }
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