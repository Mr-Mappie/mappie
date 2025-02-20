package tech.mappie.testing

import org.gradle.testkit.runner.GradleRunner
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir
import tech.mappie.BuildConfig
import java.io.File

enum class KotlinPlatform { JVM, MULTIPLATFORM }

abstract class TestBase {

    @TempDir
    protected lateinit var directory: File

    protected lateinit var runner: GradleRunner

    protected open val platform: KotlinPlatform = KotlinPlatform.JVM

    protected open val gradleVersion: String? = null

    protected open val kotlinVersion = "2.1.10"

    @BeforeEach
    fun setup() {
        runner = GradleRunner.create().apply {
            forwardOutput()
            withProjectDir(directory)
            gradleVersion?.let { withGradleVersion(gradleVersion) }
        }

        gradleVersion?.let { println("Using Gradle version $it") }
        println("Using Kotlin version $kotlinVersion")

        kotlin("settings.gradle.kts",
            """
            pluginManagement {
                repositories {
                    mavenLocal()
                    gradlePluginPortal()
                }
             }
            """.trimIndent()
        )

        when (platform) {
            KotlinPlatform.JVM -> jvm()
            KotlinPlatform.MULTIPLATFORM -> multiplatform()
        }
    }

    protected fun kotlin(file: String, @Language("kotlin") code: String) {
        directory.resolve(file).apply {
            appendText(code)
        }
    }

    protected fun java(file: String, @Language("java") code: String) {
        directory.resolve(file).apply {
            appendText(code)
        }
    }

    private fun jvm() {
        directory.resolve("src/main/kotlin").mkdirs()
        directory.resolve("src/main/java").mkdirs()
        directory.resolve("src/test/kotlin").mkdirs()

        kotlin("build.gradle.kts",
            """
            plugins {
                id("org.jetbrains.kotlin.jvm") version "$kotlinVersion"
                id("tech.mappie.plugin") version "$VERSION"
            }

            repositories {
                mavenLocal()
                mavenCentral()
            }

            dependencies {
                testImplementation(kotlin("test"))
            }

            tasks.test {
                useJUnitPlatform()
                testLogging {
                    exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL       
                }
            }

            """.trimIndent()
        )
    }

    private fun multiplatform() {
        listOf("common", "jvm", "mingwX64", "ios").forEach { platform ->
            directory.resolve("src/${platform}Main/kotlin").mkdirs()
            directory.resolve("src/${platform}Test/kotlin").mkdirs()
        }

        kotlin("build.gradle.kts",
            """
            plugins {
                id("org.jetbrains.kotlin.multiplatform") version "$kotlinVersion"
                id("tech.mappie.plugin") version "$VERSION"
            }

            repositories {
                mavenLocal()
                mavenCentral()
            }

            kotlin {
                applyDefaultHierarchyTemplate()
            
                jvm()
                mingwX64()
                iosX64()
                
                sourceSets {
                    commonTest.dependencies {
                        implementation(kotlin("test"))
                    }
                }
            }
            """.trimIndent()
        )
    }

    companion object {
        private const val VERSION = BuildConfig.VERSION

        @BeforeAll
        @JvmStatic
        fun start() {
            println("Using mappie version $VERSION")
        }
    }
}