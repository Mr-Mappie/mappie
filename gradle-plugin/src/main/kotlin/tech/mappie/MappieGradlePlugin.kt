package tech.mappie

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.plugin.*

@Suppress("unused")
class MappieGradlePlugin : KotlinCompilerPluginSupportPlugin {

    override fun apply(target: Project) {
        target.extensions.create("mappie", MappieExtension::class.java)
        target.checkCompatibility()
        target.addMappieDependency()
    }

    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        with (kotlinCompilation.project) {
            logger.info("Mappie plugin ${getPluginArtifact().version} applied")

            val extension = extensions.getByType(MappieExtension::class.java)
            return provider {
                buildList {
                    extension.warningsAsErrors.orNull?.apply {
                        add(SubpluginOption("warnings-as-errors", this.toString()))
                    }
                    extension.useDefaultArguments.orNull?.apply {
                        add(SubpluginOption("use-default-arguments", this.toString()))
                    }
                    extension.strictness.enums.orNull?.apply {
                        add(SubpluginOption("strict-enums", this.toString()))
                    }
                    extension.strictness.visibility.orNull?.apply {
                        add(SubpluginOption("strict-visibility", this.toString()))
                    }
                }
            }
        }
    }

    override fun getCompilerPluginId(): String =
        BuildConfig.COMPILER_PLUGIN_ID

    override fun getPluginArtifact(): SubpluginArtifact =
        SubpluginArtifact(
            groupId = BuildConfig.GROUP_ID,
            artifactId = BuildConfig.PLUGIN_ID,
            version = BuildConfig.VERSION,
        )

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>) = true

    private fun Project.checkCompatibility() {
        val version = getKotlinPluginVersion()
        if (SUPPORTED_KOTLIN_VERSIONS.none { regex -> regex.matches(version) }) {
            logger.warn("Mappie unsupported Kotlin version '$version'. this may lead to compilation failure.")
        }
    }

    private fun Project.addMappieDependency() {
        val dependency = dependencies.create("tech.mappie:mappie-api:${getPluginArtifact().version}")

        plugins.withId("org.jetbrains.kotlin.jvm") {
            dependencies.add("implementation", dependency)
            dependencies.add("testImplementation", dependency)

            plugins.withId("java-test-fixtures") {
                dependencies.add("testFixturesImplementation", dependency)
            }
        }

        plugins.withId("org.jetbrains.kotlin.multiplatform") {
            dependencies.add("commonMainImplementation", dependency)
            dependencies.add("commonTestImplementation", dependency)
        }
    }

    private companion object {
        private val SUPPORTED_KOTLIN_VERSIONS = listOf(
            Regex("1\\.9\\.[0-9]+(-.+)?"), // Versions 1.9.y
            Regex("2\\.[0-9]+\\.[0-9]+(-.+)?"), // Versions 2.x.y
        )
    }
}