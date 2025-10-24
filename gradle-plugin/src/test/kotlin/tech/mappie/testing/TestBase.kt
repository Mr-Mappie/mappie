package tech.mappie.testing

import org.gradle.testkit.runner.GradleRunner
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir
import tech.mappie.BuildConfig
import tech.mappie.testing.gradle.SettingsGradleBuilder
import java.io.File

enum class KotlinPlatform { JVM, MULTIPLATFORM }

enum class MappieModules { MODULE_KOTLINX_DATETIME }

abstract class TestBase {

    @TempDir
    protected lateinit var directory: File

    protected lateinit var runner: GradleRunner

    protected open val platform: KotlinPlatform = KotlinPlatform.JVM

    protected open val modules: Set<MappieModules> = emptySet()

    protected open val gradleVersion: String? = null

    protected open val kotlinVersion = "2.2.21"

    private lateinit var settings: SettingsGradleBuilder

    @BeforeEach
    fun setup() {
        settings = SettingsGradleBuilder()

        runner = GradleRunner.create().apply {
            forwardOutput()
            withProjectDir(directory)
            withDebug(true)
            gradleVersion?.let { withGradleVersion(it) }
        }

        gradleVersion?.let { println("Using Gradle version $it") }
        println("Using Kotlin version $kotlinVersion")

        text("gradle.properties",
            """
            org.gradle.jvmargs=-Xmx1024M -XX:MaxMetaspaceSize=512m
            """.trimIndent()
        )

        kotlin("settings.gradle.kts", settings.build())

        when (platform) {
            KotlinPlatform.JVM -> jvm()
            KotlinPlatform.MULTIPLATFORM -> multiplatform()
        }
    }

    protected fun text(file: String, code: String) {
        directory.resolve(file).apply {
            appendText(code)
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

            dependencies {
                ${
                    if (MappieModules.MODULE_KOTLINX_DATETIME in modules) {
                        """
                        implementation("tech.mappie:module-kotlinx-datetime:$VERSION")
                        implementation("org.jetbrains.kotlinx:kotlinx-datetime:+")
                        """.trimIndent()
                    } else {
                        ""
                    }
                }

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
            import org.jetbrains.kotlin.org.apache.commons.lang3.SystemUtils

            plugins {
                id("org.jetbrains.kotlin.multiplatform") version "$kotlinVersion"
                id("tech.mappie.plugin") version "$VERSION"
            }

            kotlin {
                applyDefaultHierarchyTemplate()
            
                jvm()
                mingwX64()

                if (SystemUtils.IS_OS_MAC) {
                    iosX64()
                }
                
                sourceSets {    
                    commonTest.dependencies {
                        implementation(kotlin("test"))
                    }
                    ${
                    if (MappieModules.MODULE_KOTLINX_DATETIME in modules) {
                    """
                    jvmMain.dependencies {
                        implementation("tech.mappie:module-kotlinx-datetime:$VERSION")
                        implementation("org.jetbrains.kotlinx:kotlinx-datetime:+")
                    }
                    """.trimIndent()
                    } else {
                        ""
                    }}
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