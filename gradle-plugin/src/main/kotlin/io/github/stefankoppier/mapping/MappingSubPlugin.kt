package io.github.stefankoppier.mapping

import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

class MappingSubPlugin : KotlinCompilerPluginSupportPlugin {
    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        TODO()
    }

    override fun getCompilerPluginId(): String = "mapping"

    override fun getPluginArtifact(): SubpluginArtifact =
        SubpluginArtifact(
            groupId = "io.github.stefankoppier",
            artifactId = "mapping",
            version = "0.0.1"
        )

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>): Boolean = true
}