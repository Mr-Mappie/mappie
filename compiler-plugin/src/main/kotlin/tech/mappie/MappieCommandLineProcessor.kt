package tech.mappie

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey

@ExperimentalCompilerApi
class MappieCommandLineProcessor : CommandLineProcessor {

    override val pluginId = "mappie"

    override val pluginOptions = listOf(
        CliOption(
            optionName = OPTION_USE_DEFAULT_ARGUMENTS,
            valueDescription = "boolean",
            description = "allow automatic resolving via default argument values",
            required = false,
        ),
        CliOption(
            optionName = OPTION_WARNINGS_AS_ERRORS,
            valueDescription = "boolean",
            description = "report all warnings as errors instead",
            required = false,
        ),
        CliOption(
            optionName = OPTION_STRICTNESS_ENUMS,
            valueDescription = "boolean",
            description = "strictness of enum validation",
            required = false,
        ),
        CliOption(
            optionName = OPTION_STRICTNESS_VISIBILITY,
            valueDescription = "boolean",
            description = "strictness of visibility modifiers",
            required = false,
        )
    )

    override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) {
        return when (option.optionName) {
            OPTION_WARNINGS_AS_ERRORS -> configuration.put(ARGUMENT_WARNINGS_AS_ERRORS, value.toBooleanStrict())
            OPTION_USE_DEFAULT_ARGUMENTS -> configuration.put(ARGUMENT_USE_DEFAULT_ARGUMENTS, value.toBooleanStrict())
            OPTION_STRICTNESS_ENUMS -> configuration.put(ARGUMENT_STRICTNESS_ENUMS, value.toBooleanStrict())
            OPTION_STRICTNESS_VISIBILITY -> configuration.put(ARGUMENT_STRICTNESS_VISIBILITY, value.toBooleanStrict())
            else -> throw IllegalArgumentException("Unknown option ${option.optionName}")
        }
    }

    companion object {
        const val OPTION_WARNINGS_AS_ERRORS = "warningsAsErrors"
        const val OPTION_USE_DEFAULT_ARGUMENTS = "useDefaultArguments"
        const val OPTION_STRICTNESS_ENUMS = "strictness.enums"
        const val OPTION_STRICTNESS_VISIBILITY = "strictness.visibility"

        val ARGUMENT_WARNINGS_AS_ERRORS = CompilerConfigurationKey<Boolean>(OPTION_WARNINGS_AS_ERRORS)
        val ARGUMENT_USE_DEFAULT_ARGUMENTS = CompilerConfigurationKey<Boolean>(OPTION_USE_DEFAULT_ARGUMENTS)
        val ARGUMENT_STRICTNESS_ENUMS = CompilerConfigurationKey<Boolean>(OPTION_STRICTNESS_ENUMS)
        val ARGUMENT_STRICTNESS_VISIBILITY = CompilerConfigurationKey<Boolean>(OPTION_STRICTNESS_VISIBILITY)
    }
}