plugins {
    id("mappie-api-convention")
    id("maven-publish")
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
            mappiePom(name = "tech.mappie:mappie-api")
        }
    }
}

dependencies {
    jvmTestImplementation(testFixtures(project(":compiler-plugin")))
    jvmTestImplementation(kotlin("test"))
    jvmTestImplementation(libs.assertj.core)
}