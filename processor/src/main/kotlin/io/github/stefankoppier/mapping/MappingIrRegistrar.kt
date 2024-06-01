package io.github.stefankoppier.mapping

import io.github.stefankoppier.mapping.generation.IrTransformer
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

class MappingIrRegistrar(private val messageCollector: MessageCollector) : IrGenerationExtension {

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val context = MappingPluginContext(messageCollector, pluginContext)

//        messageCollector.error(moduleFragment.dump())

        moduleFragment.accept(IrTransformer(context), null)
    }
}