package tech.mappie

import org.apache.maven.plugin.MojoExecution
import org.apache.maven.project.MavenProject
import org.codehaus.plexus.component.annotations.*
import org.codehaus.plexus.logging.Logger
import org.jetbrains.kotlin.maven.*

@Component(role = KotlinMavenPluginExtension::class, hint = "mappie")
class MappieMavenPlugin : KotlinMavenPluginExtension {

    @Requirement
    lateinit var logger: Logger

    override fun getCompilerPluginId() = PLUGIN_ID

    override fun getPluginOptions(project: MavenProject, execution: MojoExecution): List<PluginOption> {
        logger.debug("Loaded Maven plugin " + javaClass.name)
        return listOf()
    }

    override fun isApplicable(project: MavenProject, execution: MojoExecution) = true

    companion object {
        private const val PLUGIN_ID = BuildConfig.COMPILER_PLUGIN_ID
    }
}
