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
            optionName = OPTION_STRICTNESS_JAVA_NULLABILITY,
            valueDescription = "boolean",
            description = "strictness of java nullability validation",
            required = false,
        ),
        CliOption(
            optionName = OPTION_STRICTNESS_VISIBILITY,
            valueDescription = "boolean",
            description = "strictness of visibility modifiers",
            required = false,
        ),
        CliOption(
            optionName = OPTION_REPORT_ENABLED,
            valueDescription = "boolean",
            description = "report enabled",
            required = false,
        ),
        CliOption(
            optionName = OPTION_REPORT_DIR,
            valueDescription = "string",
            description = "report directory",
            required = false,
        )
    )

    override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) {
        return when (option.optionName) {
            OPTION_WARNINGS_AS_ERRORS -> configuration.put(ARGUMENT_WARNINGS_AS_ERRORS, value.toBooleanStrict())
            OPTION_USE_DEFAULT_ARGUMENTS -> configuration.put(ARGUMENT_USE_DEFAULT_ARGUMENTS, value.toBooleanStrict())
            OPTION_STRICTNESS_ENUMS -> configuration.put(ARGUMENT_STRICTNESS_ENUMS, value.toBooleanStrict())
            OPTION_STRICTNESS_JAVA_NULLABILITY -> configuration.put(ARGUMENT_STRICTNESS_JAVA_NULLABILITY, value.toBooleanStrict())
            OPTION_STRICTNESS_VISIBILITY -> configuration.put(ARGUMENT_STRICTNESS_VISIBILITY, value.toBooleanStrict())
            OPTION_REPORT_ENABLED -> configuration.put(ARGUMENT_REPORT_ENABLED, value.toBooleanStrict())
            OPTION_REPORT_DIR -> configuration.put(ARGUMENT_REPORT_DIR, value)
            else -> throw IllegalArgumentException("Unknown option ${option.optionName}")
        }
    }

    companion object {
        const val OPTION_WARNINGS_AS_ERRORS = "warnings-as-errors"
        const val OPTION_USE_DEFAULT_ARGUMENTS = "use-default-arguments"
        const val OPTION_STRICTNESS_ENUMS = "strict-enums"
        const val OPTION_STRICTNESS_JAVA_NULLABILITY = "strict-platform-type-nullability"
        const val OPTION_STRICTNESS_VISIBILITY = "strict-visibility"
        const val OPTION_REPORT_ENABLED = "report-enabled"
        const val OPTION_REPORT_DIR = "report-dir"

        val ARGUMENT_WARNINGS_AS_ERRORS = CompilerConfigurationKey<Boolean>(OPTION_WARNINGS_AS_ERRORS)
        val ARGUMENT_USE_DEFAULT_ARGUMENTS = CompilerConfigurationKey<Boolean>(OPTION_USE_DEFAULT_ARGUMENTS)
        val ARGUMENT_STRICTNESS_ENUMS = CompilerConfigurationKey<Boolean>(OPTION_STRICTNESS_ENUMS)
        val ARGUMENT_STRICTNESS_JAVA_NULLABILITY = CompilerConfigurationKey<Boolean>(OPTION_STRICTNESS_JAVA_NULLABILITY)
        val ARGUMENT_STRICTNESS_VISIBILITY = CompilerConfigurationKey<Boolean>(OPTION_STRICTNESS_VISIBILITY)
        val ARGUMENT_REPORT_ENABLED = CompilerConfigurationKey<Boolean>(OPTION_REPORT_ENABLED)
        val ARGUMENT_REPORT_DIR = CompilerConfigurationKey<String>(OPTION_REPORT_DIR)
    }
}