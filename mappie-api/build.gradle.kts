plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.dokka)
    id("maven-publish")
}

kotlin {
    explicitApi()

    withSourcesJar()

    jvm()
}

tasks.register<Jar>("javadocJar") {
    group = "build"
    description = "Assemble a javadoc jar containing the Dokka pages of the 'main' feature."
    archiveClassifier = "javadoc"
    from(layout.buildDirectory.dir("dokka/javadoc"))
    dependsOn("dokkaJavadoc")
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
            pom {
                name = "tech.mappie:compiler-plugin"
                description = "Kotlin Compiler Plugin for generating object mappers"
                url = "https://github.com/Mr-Mappie/mappie"

                developers {
                    developer {
                        id = "stefankoppier"
                        name = "Stefan Koppier"
                    }
                }

                scm {
                    connection = "scm:git:git://github.com/Mr-Mappie/mappie.git"
                    developerConnection = "scm:git:git://github.com/Mr-Mappie/mappie.git"
                    url = "https://github.com/Mr-Mappie/mappie/tree/main"
                }

                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }

                issueManagement {
                    system = "GitHub"
                    url = "https://github.com/Mr-Mappie/mappie/issues"
                }
            }
        }
    }
}