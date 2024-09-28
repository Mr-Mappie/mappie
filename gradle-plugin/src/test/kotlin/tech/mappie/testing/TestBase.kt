package tech.mappie.testing

import org.gradle.testkit.runner.GradleRunner
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.util.*

abstract class TestBase {

    @TempDir
    protected lateinit var directory: File

    protected lateinit var runner: GradleRunner

    private val version = javaClass.classLoader.getResourceAsStream("version.properties").use {
        Properties().apply { load(it) }.getProperty("version")
    }

    @BeforeEach
    fun setup() {
        runner = GradleRunner.create().apply {
            forwardOutput()
            withPluginClasspath()
            withProjectDir(directory)
        }

        directory.resolve("src/main/kotlin").mkdirs()
        directory.resolve("src/test/kotlin").mkdirs()

        kotlin("build.gradle.kts",
            """
            plugins {
                id("org.jetbrains.kotlin.jvm") version "2.0.20"
                id("tech.mappie.plugin")
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
            }

            """.trimIndent()
        )
    }

    protected fun kotlin(file: String, @Language("kotlin") code: String) {
        directory.resolve(file).apply {
            appendText(code)
        }
    }
}