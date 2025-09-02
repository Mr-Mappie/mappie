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
        jvmMain.dependencies {
            implementation(project(":mappie-api"))
            implementation(libs.kotlinx.datetime)
        }

        jvmTest.dependencies {
            implementation(kotlin("test"))
            implementation(project(":testutil"))
            implementation(libs.assertj.core)
        }
    }
}


publishing {
    repositories {
        maven {
            url = uri(layout.buildDirectory.dir("staging-deploy"))
        }
    }

    publications.configureEach {
        if (this is MavenPublication) {
            artifact(tasks["javadocJar"])
            // jreleaser workaround
            if (name != "jvm" && name != "kotlinMultiplatform") {
                artifact(tasks["emptyJar"])
            }
            mappiePom(name = "tech.mappie:module-kotlinx-datetime")
        }
    }
}