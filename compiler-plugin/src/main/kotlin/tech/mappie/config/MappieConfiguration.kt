package tech.mappie.config

import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import tech.mappie.MappieCommandLineProcessor.Companion.ARGUMENT_STRICTNESS_ENUMS
import tech.mappie.MappieCommandLineProcessor.Companion.ARGUMENT_STRICTNESS_VISIBILITY
import tech.mappie.MappieCommandLineProcessor.Companion.ARGUMENT_USE_DEFAULT_ARGUMENTS
import tech.mappie.MappieCommandLineProcessor.Companion.ARGUMENT_WARNINGS_AS_ERRORS

data class MappieConfiguration(
    val warningsAsErrors: Boolean,
    val useDefaultArguments: Boolean,
    val strictness: StrictnessConfiguration,
) {

    companion object {
        @OptIn(ExperimentalCompilerApi::class)
        fun of(configuration: CompilerConfiguration) =
            MappieConfiguration(
                warningsAsErrors = configuration.get(ARGUMENT_WARNINGS_AS_ERRORS, false),
                useDefaultArguments = configuration.get(ARGUMENT_USE_DEFAULT_ARGUMENTS, true),
                strictness = StrictnessConfiguration(
                    enums = configuration.get(ARGUMENT_STRICTNESS_ENUMS, true),
                    visibility = configuration.get(ARGUMENT_STRICTNESS_VISIBILITY, false)
                )
            )
    }
}

data class StrictnessConfiguration(
    val enums: Boolean,
    val visibility: Boolean,
)