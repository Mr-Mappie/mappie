package io.github.mappie

import org.jetbrains.kotlin.compiler.plugin.CliOption
import org.jetbrains.kotlin.compiler.plugin.CommandLineProcessor
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

@ExperimentalCompilerApi
class MappieCommandLineProcessor : CommandLineProcessor {

    override val pluginId = "mappie"

    override val pluginOptions = emptyList<CliOption>()
}