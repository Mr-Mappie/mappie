package tech.mappie

import org.apache.maven.model.Plugin
import org.apache.maven.plugin.MojoExecution
import org.apache.maven.project.MavenProject
import org.codehaus.plexus.logging.Logger
import org.jetbrains.kotlin.maven.*
import java.io.File
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Named("mappie")
@Singleton
class MappieMavenPlugin : KotlinMavenPluginExtension {

    @Inject
    lateinit var logger: Logger

    override fun getCompilerPluginId() = PLUGIN_ID

    override fun getPluginOptions(project: MavenProject, execution: MojoExecution): List<PluginOption> {
        logger.debug("Loaded Maven plugin " + javaClass.name)
        return buildList {
            add(
                PluginOption(
                    PLUGIN_ID,
                    PLUGIN_ID,
                    "report-dir",
                    File(project.basedir, "target/mappie").absolutePath,
                )
            )
            add(
                PluginOption(
                    PLUGIN_ID,
                    PLUGIN_ID,
                    "output-dir",
                    stateDirectoryOf(project, execution.goal).absolutePath,
                )
            )
            if (execution.goal == GOAL_TEST_COMPILE) {
                add(
                    PluginOption(
                        PLUGIN_ID,
                        PLUGIN_ID,
                        "input-dirs",
                        stateDirectoryOf(project, GOAL_COMPILE).absolutePath,
                    )
                )
            }
        }
    }

    private fun stateDirectoryOf(project: MavenProject, goal: String) =
        File(project.basedir, "target/mappie/state/$goal")

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
        private const val GOAL_COMPILE = "compile"
        private const val GOAL_TEST_COMPILE = "test-compile"
        private val EXPECTED_KOTLIN_VERSION = BuildConfig.VERSION.split('-').first()
    }
}
