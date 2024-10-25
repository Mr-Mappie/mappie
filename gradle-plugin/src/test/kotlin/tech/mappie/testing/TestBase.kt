package tech.mappie.testing

import org.gradle.testkit.runner.GradleRunner
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.util.*

abstract class TestBase {

    @TempDir
    protected lateinit var directory: File

    protected lateinit var runner: GradleRunner

    protected open val kotlinVersion = "2.0.21"

    @BeforeEach
    fun setup() {
        runner = GradleRunner.create().apply {
            forwardOutput()
            withProjectDir(directory)
        }

        directory.resolve("src/main/kotlin").mkdirs()
        directory.resolve("src/main/java").mkdirs()
        directory.resolve("src/test/kotlin").mkdirs()

        println("Using kotlin version $kotlinVersion")

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

        kotlin("build.gradle.kts",
            """
            plugins {
                id("org.jetbrains.kotlin.jvm") version "$kotlinVersion"
                id("tech.mappie.plugin") version "$version"
            }

            dependencies {
                implementation("tech.mappie:mappie-api:$version")          
                testImplementation(kotlin("test"))
            }

            repositories {
                mavenLocal()
                mavenCentral()
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

    companion object {
        private val version = javaClass.classLoader.getResourceAsStream("version.properties").use {
            Properties().apply { load(it) }.getProperty("version")
        }

        @BeforeAll
        @JvmStatic
        fun start() {
            println("Using mappie version $version")
        }
    }
}