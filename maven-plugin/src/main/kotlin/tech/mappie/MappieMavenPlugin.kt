package tech.mappie

import org.apache.maven.model.Plugin
import org.apache.maven.plugin.MojoExecution
import org.apache.maven.project.MavenProject
import org.codehaus.plexus.component.annotations.*
import org.codehaus.plexus.logging.Logger
import org.jetbrains.kotlin.maven.*
import java.io.File

@Component(role = KotlinMavenPluginExtension::class, hint = "mappie")
class MappieMavenPlugin : KotlinMavenPluginExtension {

    @Requirement
    lateinit var logger: Logger

    override fun getCompilerPluginId() = PLUGIN_ID

    override fun getPluginOptions(project: MavenProject, execution: MojoExecution): List<PluginOption> {
        logger.debug("Loaded Maven plugin " + javaClass.name)
        return listOf(
            PluginOption(
                "mappie",
                "mappie",
                "report-dir",
                File(project.basedir, "target/mappie").absolutePath,
            )
        )
    }

    override fun isApplicable(project: MavenProject, execution: MojoExecution): Boolean {
        val version = project.buildPlugins
            .filterIsInstance<Plugin>()
            .firstOrNull { it.key == "org.jetbrains.kotlin:kotlin-maven-plugin" }
            ?.version

        if (version != EXPECTED_KOTLIN_VERSION) {
            logger.warn("Mappie unsupported Kotlin version $version, $EXPECTED_KOTLIN_VERSION was expected. This is highly likely to lead to compilation failure.")
        }

        return true
    }

    companion object {
        private const val PLUGIN_ID = BuildConfig.COMPILER_PLUGIN_ID
        private val EXPECTED_KOTLIN_VERSION = BuildConfig.VERSION.split('-').first()
    }
}
