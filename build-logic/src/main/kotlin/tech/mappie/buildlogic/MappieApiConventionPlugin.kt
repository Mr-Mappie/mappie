package tech.mappie.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.testing.Test
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.jetbrains.dokka.gradle.tasks.DokkaGeneratePublicationTask

class MappieApiConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("convention-mappie")
        pluginManager.apply("org.jetbrains.kotlin.multiplatform")
        pluginManager.apply("org.jetbrains.dokka")

        registerDocumentationArtifacts()
        configureTests()
    }

    private fun Project.registerDocumentationArtifacts() {
        val dokkaHtml = tasks.named("dokkaGeneratePublicationHtml", DokkaGeneratePublicationTask::class.java)
        tasks.register("javadocJar", Jar::class.java) {
            group = LifecycleBasePlugin.BUILD_GROUP
            description = "Assemble a javadoc jar containing the Dokka pages of the 'main' feature."
            archiveClassifier.set("javadoc")
            from(dokkaHtml.flatMap { it.outputDirectory })
            dependsOn(dokkaHtml)
        }

        tasks.register("emptyJar", Jar::class.java) {
            group = LifecycleBasePlugin.BUILD_GROUP
            description = "Assemble an empty jar."
        }
    }

    private fun Project.configureTests() {
        tasks.withType(Test::class.java).configureEach {
            useJUnitPlatform()
            maxParallelForks = halfWorkerCount()
        }
    }
}
