package tech.mappie

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
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
                    extension.namingConvention.orNull?.apply {
                        add(SubpluginOption("naming-convention", this.name))
                    }
                    extension.strictness.enums.orNull?.apply {
                        add(SubpluginOption("strict-enums", this.toString()))
                    }
                    extension.strictness.platformTypeNullability.orNull?.apply {
                        add(SubpluginOption("strict-platform-type-nullability", this.toString()))
                    }
                    extension.strictness.visibility.orNull?.apply {
                        add(SubpluginOption("strict-visibility", this.toString()))
                    }
                    extension.reporting.enabled.orNull?.apply {
                        add(SubpluginOption("report-enabled", this.toString()))
                    }
                    extension.reporting.directory.convention(layout.buildDirectory.map { it.dir("mappie") }).get().apply {
                        add(SubpluginOption("report-dir", asFile.absolutePath))
                    }
                    add(SubpluginOption("output-dir", kotlinCompilation.project.buildDir.absolutePath))
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
        if (version != EXPECTED_KOTLIN_VERSION) {
            logger.warn("Mappie unsupported Kotlin version $version, $EXPECTED_KOTLIN_VERSION was expected. This is highly likely to lead to compilation failure.")
        }
    }

    private fun Project.addMappieDependency() {
        val dependency = dependencies.create("tech.mappie:mappie-api:${BuildConfig.VERSION}")

        plugins.withId("org.jetbrains.kotlin.jvm") {
            dependencies.add("implementation", dependency)
            dependencies.add("testImplementation", dependency)

            plugins.withId("java-test-fixtures") {
                dependencies.add("testFixturesImplementation", dependency)
            }
        }

        plugins.withId("org.jetbrains.kotlin.multiplatform") {
            kotlinExtension.sourceSets.named("commonMain").configure {
                it.dependencies {
                    implementation(dependency)
                }
            }
        }
    }

    companion object {
        private val EXPECTED_KOTLIN_VERSION = BuildConfig.VERSION.split('-').first()
    }
}