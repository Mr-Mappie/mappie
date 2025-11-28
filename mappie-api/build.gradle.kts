import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import tech.mappie.buildlogic.mappiePom

plugins {
    alias(libs.plugins.convention.mappie.api)
    `maven-publish`
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
            implementation(projects.testutil)
            implementation(libs.assertj.core)
        }
    }
}

publishing {
    publications.withType(MavenPublication::class.java).configureEach {
        artifact(tasks.named("javadocJar"))
        // jreleaser workaround
        if (name != "jvm" && name != "kotlinMultiplatform") {
            artifact(tasks.named("emptyJar"))
        }
        mappiePom(name = "tech.mappie:mappie-api")
    }

    if (System.getenv("RELEASE_API").toBoolean()) {
        repositories {
            maven {
                url = uri(rootProject.layout.buildDirectory.file("staging-deploy"))
            }
        }
    }
}
