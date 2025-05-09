package tech.mappie

import tech.mappie.MappieCommandLineProcessor.Companion.ARGUMENT_STRICTNESS_ENUMS
import tech.mappie.MappieCommandLineProcessor.Companion.ARGUMENT_STRICTNESS_VISIBILITY
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys.MODULE_CHUNK
import org.jetbrains.kotlin.cli.common.messages.MessageCollector.Companion.NONE
import org.jetbrains.kotlin.cli.common.modules.ModuleChunk
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter
import tech.mappie.MappieCommandLineProcessor.Companion.ARGUMENT_REPORT_DIR
import tech.mappie.MappieCommandLineProcessor.Companion.ARGUMENT_REPORT_ENABLED
import tech.mappie.MappieCommandLineProcessor.Companion.ARGUMENT_USE_DEFAULT_ARGUMENTS
import tech.mappie.MappieCommandLineProcessor.Companion.ARGUMENT_WARNINGS_AS_ERRORS
import tech.mappie.config.MappieConfiguration
import tech.mappie.fir.MappieFirRegistrar
import tech.mappie.ir.MappieIrRegistrar

@OptIn(ExperimentalCompilerApi::class)
class MappieCompilerPluginRegistrar : CompilerPluginRegistrar() {

    override val supportsK2: Boolean = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        val config = MappieConfiguration(
            isMappieDebugMode = isStartedWithTestFixtures(configuration),
            warningsAsErrors = configuration.get(ARGUMENT_WARNINGS_AS_ERRORS, false),
            useDefaultArguments = configuration.get(ARGUMENT_USE_DEFAULT_ARGUMENTS, true),
            strictEnums = configuration.get(ARGUMENT_STRICTNESS_ENUMS, true),
            strictVisiblity = configuration.get(ARGUMENT_STRICTNESS_VISIBILITY, false),
            reportEnabled = configuration.get(ARGUMENT_REPORT_ENABLED, false),
            reportDir = configuration.get(ARGUMENT_REPORT_DIR, ""),
        )
        FirExtensionRegistrarAdapter.registerExtension(MappieFirRegistrar())
        IrGenerationExtension.registerExtension(MappieIrRegistrar(configuration.get(MESSAGE_COLLECTOR_KEY, NONE), config))
    }

    private fun isStartedWithTestFixtures(configuration: CompilerConfiguration) =
        configuration.get(MODULE_CHUNK, ModuleChunk(emptyList())).modules
            .firstOrNull { it.getModuleName() == "main" }
            ?.getClasspathRoots()
            ?.any { it.matches(Regex(".*compiler-plugin-.*-test-fixtures.*\\.jar")) }
            ?: false
}
