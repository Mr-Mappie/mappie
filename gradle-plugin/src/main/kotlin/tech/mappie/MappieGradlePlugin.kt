@file:Suppress("unused")

package tech.mappie

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.*
import java.util.*

class MappieGradlePlugin : KotlinCompilerPluginSupportPlugin {

    override fun apply(target: Project) {
        target.extensions.create("mappie", MappieExtension::class.java)
        checkCompatibility(target)
    }

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val extension = kotlinCompilation.project.extensions.getByType(MappieExtension::class.java)
        kotlinCompilation.project.logger.info("Mappie plugin ${getPluginArtifact().version} applied")
        return kotlinCompilation.target.project.provider {
            buildList {
                extension.warningsAsErrors.orNull?.apply {
                    add(SubpluginOption("warningsAsErrors", this.toString()))
                }
                extension.useDefaultArguments.orNull?.apply {
                    add(SubpluginOption("useDefaultArguments", this.toString()))
                }
                extension.strictness.enums.orNull?.apply {
                    add(SubpluginOption("strictness.enums", this.toString() ))
                }
                extension.strictness.visibility.orNull?.apply {
                    add(SubpluginOption("strictness.visibility", this.toString()))
                }
            }
        }
    }

    override fun getCompilerPluginId(): String = "mappie"

    override fun getPluginArtifact(): SubpluginArtifact =
        SubpluginArtifact(
            groupId = "tech.mappie",
            artifactId = "mappie-compiler-plugin",
            version = javaClass.classLoader.getResourceAsStream("version.properties").use {
                Properties().apply { load(it) }.getProperty("version")
            },
        )

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>) =
        kotlinCompilation.target.project.plugins.hasPlugin(MappieGradlePlugin::class.java)

    private fun checkCompatibility(target: Project) {
        val version = target.getKotlinPluginVersion()
        if (version !in SUPPORTED_KOTLIN_VERSIONS) {
            target.logger.warn("Mappie: unsupported Kotlin version $version. Expected one of ${SUPPORTED_KOTLIN_VERSIONS.joinToString()}. This may lead to compilation failure.")
        }
    }

    private companion object {
        private const val KOTLIN_GRADLE_PLUGIN_NAME = "kotlin-gradle-plugin"
        private val SUPPORTED_KOTLIN_VERSIONS = listOf("1.9.24", "2.0.0", "2.0.20-Beta1")
    }
}