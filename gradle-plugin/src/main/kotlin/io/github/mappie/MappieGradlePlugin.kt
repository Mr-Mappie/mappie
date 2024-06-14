package io.github.mappie

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class MappieGradlePlugin : KotlinCompilerPluginSupportPlugin {

    override fun apply(target: Project) {
        target.extensions.create("mappie", MappieExtension::class.java)
    }

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        val extension = kotlinCompilation.project.extensions.getByType(MappieExtension::class.java)
        return kotlinCompilation.target.project.provider {
            listOf(
                SubpluginOption("strictness.enums", lazy { extension.strictness.enums.get().toString() }),
            )
        }
    }

    override fun getCompilerPluginId(): String = "mappie"

    override fun getPluginArtifact(): SubpluginArtifact =
        SubpluginArtifact(
            groupId = "io.github.mappie",
            artifactId = "compiler-plugin",
            version = "0.1.0",
        )

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>) =
        kotlinCompilation.target.project.plugins.hasPlugin(MappieGradlePlugin::class.java)
}