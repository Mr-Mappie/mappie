plugins {
    id("mappie-api-convention")
    id("maven-publish")
}

kotlin {
    explicitApi()
    withSourcesJar(publish = true)
    applyDefaultHierarchyTemplate()

    jvm {
        withSourcesJar(publish = true)
    }

    androidNativeX64()
    androidNativeArm64()

    iosArm64()
    iosSimulatorArm64()
    iosX64()

    tvosX64()
    tvosSimulatorArm64()
    tvosArm64()

    js {
        browser()
        nodejs()
    }

    @OptIn(org.jetbrains.kotlin.gradle.ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
    }

    mingwX64()

    macosX64()
    macosArm64()

    linuxX64()
    linuxArm64()

    sourceSets {
        jvmTest.dependencies {
            implementation(kotlin("test"))
            implementation(project(":testutil"))
            implementation(libs.assertj.core)
        }
    }
}

publishing {
    publications.configureEach {
        if (this is MavenPublication) {
            artifact(tasks["javadocJar"])
            // jreleaser workaround
            if (name != "jvm" && name != "kotlinMultiplatform") {
                artifact(tasks["emptyJar"])
            }
            mappiePom(name = "tech.mappie:mappie-api")
        }
    }

    if (System.getenv("RELEASE_API").toBoolean()) {
        repositories {
            maven {
                url = uri(rootProject.layout.buildDirectory.file("staging-deploy"))
            }
        }
    }
}