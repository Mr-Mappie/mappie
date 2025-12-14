package tech.mappie.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion

class MappieJvmConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("convention-mappie")
        pluginManager.apply("org.jetbrains.kotlin.jvm")

        extensions.configure(JavaPluginExtension::class.java) {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(17))
            }
        }

        tasks.withType(Test::class.java).configureEach {
            useJUnitPlatform()
            maxHeapSize = "1024m"
            maxParallelForks = halfWorkerCount()
        }
    }
}
