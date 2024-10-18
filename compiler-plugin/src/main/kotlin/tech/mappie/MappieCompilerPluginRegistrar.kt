package tech.mappie

import tech.mappie.MappieCommandLineProcessor.Companion.ARGUMENT_STRICTNESS_ENUMS
import tech.mappie.MappieCommandLineProcessor.Companion.ARGUMENT_STRICTNESS_VISIBILITY
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CommonConfigurationKeys.MESSAGE_COLLECTOR_KEY
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrarAdapter
import tech.mappie.MappieCommandLineProcessor.Companion.ARGUMENT_USE_DEFAULT_ARGUMENTS
import tech.mappie.MappieCommandLineProcessor.Companion.ARGUMENT_WARNINGS_AS_ERRORS
import tech.mappie.fir.MappieFirRegistrar
import tech.mappie.ir.MappieIrGenerationExtension

@OptIn(ExperimentalCompilerApi::class)
class MappieCompilerPluginRegistrar : CompilerPluginRegistrar() {

    override val supportsK2: Boolean = true

    override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
        FirExtensionRegistrarAdapter.registerExtension(MappieFirRegistrar())
        IrGenerationExtension.registerExtension(
            MappieIrGenerationExtension(
                configuration.get(MESSAGE_COLLECTOR_KEY, MessageCollector.NONE),
                MappieConfiguration(
                    warningsAsErrors = configuration.get(ARGUMENT_WARNINGS_AS_ERRORS, false),
                    useDefaultArguments = configuration.get(ARGUMENT_USE_DEFAULT_ARGUMENTS, true),
                    strictness = StrictnessConfiguration(
                        enums = configuration.get(ARGUMENT_STRICTNESS_ENUMS, true),
                        visibility = configuration.get(ARGUMENT_STRICTNESS_VISIBILITY, false)
                    )
                )
            )
        )
    }
}
