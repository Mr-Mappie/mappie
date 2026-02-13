package tech.mappie

import tech.mappie.MappieCommandLineProcessor.Companion.ARGUMENT_STRICTNESS_ENUMS
import tech.mappie.MappieCommandLineProcessor.Companion.ARGUMENT_STRICTNESS_VISIBILITY
import tech.mappie.MappieCommandLineProcessor.Companion.ARGUMENT_NAMING_CONVENTION
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.messages.MessageCollector.Companion.NONE
import org.jetbrains.kotlin.cli.common.moduleChunk
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter
import org.jetbrains.kotlin.konan.file.File
import tech.mappie.MappieCommandLineProcessor.Companion.ARGUMENT_OUTPUT_DIR
import tech.mappie.MappieCommandLineProcessor.Companion.ARGUMENT_REPORT_DIR
import tech.mappie.MappieCommandLineProcessor.Companion.ARGUMENT_REPORT_ENABLED
import tech.mappie.MappieCommandLineProcessor.Companion.ARGUMENT_STRICTNESS_JAVA_NULLABILITY
import tech.mappie.MappieCommandLineProcessor.Companion.ARGUMENT_USE_DEFAULT_ARGUMENTS
import tech.mappie.MappieCommandLineProcessor.Companion.ARGUMENT_WARNINGS_AS_ERRORS
import tech.mappie.compiler_plugin.BuildConfig
import tech.mappie.config.MappieConfiguration
import tech.mappie.config.MappieModule
import tech.mappie.config.options.NamingConventionMode
import tech.mappie.fir.MappieFirRegistrar
import tech.mappie.ir.MappieIrRegistrar
import java.util.EnumSet

@OptIn(ExperimentalCompilerApi::class)
class MappieCompilerPluginRegistrar : CompilerPluginRegistrar() {

    override val pluginId = "mappie"

    override val supportsK2: Boolean = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        val config = MappieConfiguration(
            modules = EnumSet.noneOf(MappieModule::class.java).apply {
                if (configuration.isStartedWithDependency(MODULE_KOTLINX_DATETIME_REGEX)) {
                    add(MappieModule.KOTLINX_DATETIME)
                }
                if (configuration.isStartedWithDependency(MODULE_KOTLINX_COLLECTIONS_IMMUTABLE_REGEX)) {
                    add(MappieModule.KOTLINX_COLLECTIONS_IMMUTABLE)
                }
            },
            isMappieDebugMode = configuration.isStartedWithDependency(TESTUTIL_REGEX),
            warningsAsErrors = configuration.get(ARGUMENT_WARNINGS_AS_ERRORS, false),
            useDefaultArguments = configuration.get(ARGUMENT_USE_DEFAULT_ARGUMENTS, true),
            namingConvention = configuration.get(ARGUMENT_NAMING_CONVENTION)?.let { NamingConventionMode.valueOf(it) } ?: NamingConventionMode.STRICT,
            strictEnums = configuration.get(ARGUMENT_STRICTNESS_ENUMS, true),
            strictplatformTypeNullability = configuration.get(ARGUMENT_STRICTNESS_JAVA_NULLABILITY, true),
            strictVisibility = configuration.get(ARGUMENT_STRICTNESS_VISIBILITY, false),
            reportEnabled = configuration.get(ARGUMENT_REPORT_ENABLED, false),
            outputDir = configuration.get(ARGUMENT_OUTPUT_DIR, "").ifEmpty { null },
            reportDir = configuration.get(ARGUMENT_REPORT_DIR, ""),
        )
        FirExtensionRegistrarAdapter.registerExtension(MappieFirRegistrar())
        IrGenerationExtension.registerExtension(MappieIrRegistrar(configuration.get(MESSAGE_COLLECTOR_KEY, NONE), config))
    }

    private fun CompilerConfiguration.isStartedWithDependency(pattern: Regex) =
        moduleChunk
            ?.modules
            ?.flatMap { it.getClasspathRoots() }
            ?.any { it.matches(pattern) }
            ?: false

    companion object {
        private val SEPARATOR = Regex.escapeReplacement(File.separator)

        private val MODULE_KOTLINX_DATETIME_REGEX = Regex(
            "(.*modules${SEPARATOR}kotlinx-datetime${SEPARATOR}build${SEPARATOR}classes${SEPARATOR}kotlin${SEPARATOR}jvm${SEPARATOR}main)|(.*module-kotlinx-datetime.*-${BuildConfig.VERSION}.*)"
        )

        private val MODULE_KOTLINX_COLLECTIONS_IMMUTABLE_REGEX = Regex(
            "(.*modules${SEPARATOR}kotlinx-collections-immutable${SEPARATOR}build${SEPARATOR}classes${SEPARATOR}kotlin${SEPARATOR}jvm${SEPARATOR}main)|(.*module-kotlinx-collections-immutable.*-${BuildConfig.VERSION}.*)"
        )

        private val TESTUTIL_REGEX = Regex(
            ".*testutil-${BuildConfig.VERSION}.*\\.jar"
        )
    }
}
