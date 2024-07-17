package tech.mappie

import org.apache.maven.plugin.MojoExecution
import org.apache.maven.project.MavenProject
import org.jetbrains.kotlin.maven.KotlinMavenPluginExtension
import org.jetbrains.kotlin.maven.PluginOption
import java.io.IOException
import java.util.*

class MappieMavenPlugin : KotlinMavenPluginExtension {
    override fun isApplicable(project: MavenProject, execution: MojoExecution): Boolean {
        return true
    }

    override fun getCompilerPluginId(): String {
        try {
            javaClass.classLoader.getResourceAsStream("version.properties").use { resource ->
                val properties = Properties()
                properties.load(resource)
                val version = properties.getProperty("version")
                return "tech.mappie:mappie-compiler-plugin:$version"
            }
        } catch (e: IOException) {
            throw IllegalStateException("Failed to load version of Mappie.")
        }
    }

    override fun getPluginOptions(project: MavenProject, execution: MojoExecution): List<PluginOption> {
        return listOf()
    }
}
