package tech.mappie

import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.externalSystem.model.DataNode
import com.intellij.openapi.externalSystem.model.project.ModuleData
import org.jetbrains.kotlin.idea.compilerPlugin.CompilerPluginSetup
import org.jetbrains.kotlin.idea.compilerPlugin.modifyCompilerArgumentsForPlugin
import org.jetbrains.kotlin.idea.facet.KotlinFacet
import org.jetbrains.kotlin.idea.gradleJava.configuration.GradleProjectImportHandler
import org.jetbrains.plugins.gradle.model.data.GradleSourceSetData

class MappieGradleProjectImportHandler : GradleProjectImportHandler {
    private val compilerPluginId = "tech.mappie.mappie-compiler-plugin"
    private val pluginName = "mappie"

    private val log = logger<MappieGradleProjectImportHandler>()

    override fun importByModule(facet: KotlinFacet, moduleNode: DataNode<ModuleData>) {
        log.warn("Trying to add mappie compiler plugin to module ${moduleNode.data.moduleName}")
        modifyCompilerArgumentsForPlugin(
            facet,
            getPluginSetupByModule(),
            compilerPluginId,
            pluginName,
        )
    }

    override fun importBySourceSet(facet: KotlinFacet, sourceSetNode: DataNode<GradleSourceSetData>) {
        log.warn("Trying to add mappie compiler plugin to source set ${sourceSetNode.data.moduleName}")
        modifyCompilerArgumentsForPlugin(
            facet,
            getPluginSetupByModule(),
            compilerPluginId,
            pluginName,
        )
    }

    private fun getPluginSetupByModule(): CompilerPluginSetup {
        return CompilerPluginSetup(
            emptyList(),
            listOf("C:\\Users\\stefa\\.m2\\repository\\tech\\mappie\\mappie-compiler-plugin\\1.0.0-SNAPSHOT\\mappie-compiler-plugin-1.0.0-SNAPSHOT.jar")
        )
    }
}