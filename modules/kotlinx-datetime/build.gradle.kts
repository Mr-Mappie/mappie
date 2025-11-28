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

    sourceSets {
        commonMain.dependencies {
            implementation(projects.mappieApi)
        }

        jvmMain.dependencies {
            implementation(libs.kotlinx.datetime)
        }

        jvmTest.dependencies {
            implementation(projects.testutil)
            implementation(kotlin("test"))
            implementation(libs.assertj.core)
        }
    }
}

publishing {
    publications.withType(MavenPublication::class.java).configureEach {
        artifactId = artifactId.replace("kotlinx-datetime", "module-kotlinx-datetime")
        artifact(tasks.named("javadocJar"))
        // jreleaser workaround
        if (name != "jvm" && name != "kotlinMultiplatform") {
            artifact(tasks.named("emptyJar"))
        }
        mappiePom(name = "tech.mappie:module-kotlinx-datetime")
    }

    if (System.getenv("RELEASE_MODULE_KOTLINX_DATETIME").toBoolean()) {
        repositories {
            maven {
                url = uri(rootProject.layout.buildDirectory.file("staging-deploy"))
            }
        }
    }
}
