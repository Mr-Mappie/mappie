import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    id("maven-publish")
}

dependencies {
    compileOnly(libs.kotlin.compiler.embeddable)

    implementation(project(":mappie-api"))

    testImplementation(kotlin("test"))
    testImplementation(libs.kotlin.compiler.embeddable)
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("kotlin") {
            artifactId = "mappie-compiler-plugin"
            from(components["java"])

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
                    licenses {
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

    repositories {
        maven {
            url = uri(layout.buildDirectory.dir("staging-deploy"))
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.freeCompilerArgs.add(
        "-opt-in=org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI"
    )
}