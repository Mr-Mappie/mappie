package io.github.mappie

import org.jetbrains.kotlin.compiler.plugin.AbstractCliOption
import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.config.CompilerConfigurationKey

@ExperimentalCompilerApi
class MappieCommandLineProcessor : CommandLineProcessor {

    override val pluginId = "mappie"

    override val pluginOptions = listOf<CliOption>(
        CliOption(
            optionName = OPTION_STRICTNESS_ENUMS,
            valueDescription = "boolean",
            description = "strictness of enum validation",
            required = false,
        )
    )

    override fun processOption(option: AbstractCliOption, value: String, configuration: CompilerConfiguration) {
        return when (option.optionName) {
            OPTION_STRICTNESS_ENUMS -> configuration.put(ARGUMENT_STRICTNESS_ENUMS, value.toBooleanStrict())
            else -> throw IllegalArgumentException("Unknown option ${option.optionName}")
        }
    }

    companion object {
        const val OPTION_STRICTNESS_ENUMS = "strictness.enums"
        val ARGUMENT_STRICTNESS_ENUMS = CompilerConfigurationKey<Boolean>(OPTION_STRICTNESS_ENUMS)
    }
}