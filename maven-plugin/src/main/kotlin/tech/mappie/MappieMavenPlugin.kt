package tech.mappie

import org.apache.maven.plugin.MojoExecution
import org.apache.maven.project.MavenProject
import org.codehaus.plexus.component.annotations.Component
import org.codehaus.plexus.component.annotations.Requirement
import org.codehaus.plexus.logging.Logger
import org.jetbrains.kotlin.maven.KotlinMavenPluginExtension
import org.jetbrains.kotlin.maven.PluginOption

@Component(role = KotlinMavenPluginExtension::class, hint = "mappie")
class MappieMavenPlugin : KotlinMavenPluginExtension {

    @Requirement
    lateinit var logger: Logger

    override fun getCompilerPluginId() = "tech.mappie"

    override fun getPluginOptions(project: MavenProject, execution: MojoExecution): List<PluginOption> {
        logger.debug("Loaded Maven plugin " + javaClass.name)
        return listOf()
    }

    override fun isApplicable(project: MavenProject, execution: MojoExecution) = true
}
