import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.kotlin.jvm)
    id("java-test-fixtures")
    id("maven-publish")
    id("jacoco")
}

dependencies {
    compileOnly(libs.kotlin.compiler.embeddable)

    implementation(project(":mappie-api"))

    testFixturesImplementation(project(":mappie-api"))
    testFixturesImplementation(libs.kotlin.compiler.embeddable)
    testFixturesImplementation(libs.classgraph)
    testFixturesImplementation(libs.okio)
    testFixturesImplementation(libs.assertj.core)

    testImplementation(project(":mappie-api"))
    testImplementation(kotlin("reflect"))
    testImplementation(kotlin("test"))
    testImplementation(libs.assertj.core)
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

            from((components["java"] as AdhocComponentWithVariants).apply {
                withVariantsFromConfiguration(configurations["testFixturesApiElements"]) { skip() }
                withVariantsFromConfiguration(configurations["testFixturesRuntimeElements"]) { skip() }
            })

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

    repositories {
        maven {
            url = uri(layout.buildDirectory.dir("staging-deploy"))
        }
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.freeCompilerArgs.addAll(
        "-opt-in=org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI",
        "-opt-in=org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi"
    )
}
