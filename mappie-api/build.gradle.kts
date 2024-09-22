@file:OptIn(ExperimentalWasmDsl::class)

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.dokka)
    alias(libs.plugins.android.library) apply false
    id("maven-publish")
}

kotlin {
    explicitApi()
    withSourcesJar(publish = false)
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

    wasmJs {
        browser()
        nodejs()
    }

    mingwX64()

    macosX64()
    macosArm64()

    linuxX64()
    linuxArm64()
}

val dokkaHtml by tasks.dokkaHtml
tasks.register<Jar>("javadocJar") {
    group = "build"
    description = "Assemble a javadoc jar containing the Dokka pages of the 'main' feature."
    archiveClassifier = "javadoc"
    from(dokkaHtml.outputDirectory)
    dependsOn(dokkaHtml)
}

tasks.register<Jar>("emptyJar") {
    group = "build"
    description = "Assemble an empty jar."
}

publishing {
    repositories {
        maven {
            url = uri(layout.buildDirectory.dir("staging-deploy"))
        }
    }

    publications.configureEach {
        if (this is MavenPublication) {
            // jreleaser workaround
            if (name != "jvm" && name != "kotlinMultiplatform") {
                artifact(tasks["emptyJar"])
            }
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