package io.github.mappie

import io.github.mappie.generation.IrTransformer
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

class MappieIrRegistrar(private val messageCollector: MessageCollector) : IrGenerationExtension {

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        context = MappiePluginContext(messageCollector, pluginContext)
        moduleFragment.accept(IrTransformer(), null)
    }

    companion object {
        lateinit var context: MappiePluginContext
    }
}