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

    sourceSets {
        commonMain.dependencies {
            implementation(project(":mappie-api"))
        }

        jvmMain.dependencies {
            implementation(libs.kotlinx.datetime)
        }

        jvmTest.dependencies {
            implementation(kotlin("test"))
            implementation(project(":mappie-api"))
            implementation(project(":testutil"))
            implementation(libs.assertj.core)
        }
    }
}

publishing {
    if (System.getenv("RELEASE_MODULE_KOTLINX_DATETIME").toBoolean()) {
        publications.configureEach {
            if (this is MavenPublication) {
                artifactId = artifactId.replace("kotlinx-datetime", "module-kotlinx-datetime")
                artifact(tasks["javadocJar"])
                // jreleaser workaround
                if (name != "jvm" && name != "kotlinMultiplatform") {
                    artifact(tasks["emptyJar"])
                }
                mappiePom(name = "tech.mappie:module-kotlinx-datetime")
            }
        }
    }

    repositories {
        maven {
            url = uri(rootProject.layout.buildDirectory.file("staging-deploy"))
        }
    }
}