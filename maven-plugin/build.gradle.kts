plugins {
    alias(libs.plugins.kotlin.jvm)
    id("maven-publish")
}

dependencies {
    compileOnly("org.apache.maven:maven-project:2.2.1")
    compileOnly("org.apache.maven:maven-core:3.9.8")
    compileOnly("org.jetbrains.kotlin:kotlin-maven-plugin:2.0.0")
}

publishing {
    repositories {
        maven {
            url = uri(layout.buildDirectory.dir("staging-deploy"))
        }
    }
    publications {
        create<MavenPublication>("java-maven-plugin") {
            artifactId = "mappie-maven-plugin"

            from(components["java"])

            pom {
//                artifact(tasks["javadocJar"])
                pom {
                    name = "tech.mappie:maven-plugin"
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
}

tasks.register("updateCompilerPluginVersion") {
    group = "build"
    description = "Update version.properties file for Gradle plugin."
    doLast {
        val directory = project.mkdir("src/main/resources")
        File(directory, "version.properties").writeText("version=${project.version}")
    }
}

tasks.compileKotlin {
    dependsOn("updateCompilerPluginVersion")
}