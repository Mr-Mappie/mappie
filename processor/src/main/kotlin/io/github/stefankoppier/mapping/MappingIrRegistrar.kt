package io.github.stefankoppier.mapping

import io.github.stefankoppier.mapping.traversal.IrTransformer
import io.github.stefankoppier.mapping.util.error
import io.github.stefankoppier.mapping.util.warn
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.dump

class MappingIrRegistrar(private val messageCollector: MessageCollector) : IrGenerationExtension {

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val context = MappingPluginContext(messageCollector, pluginContext)
//        context.messageCollector.error(moduleFragment.dump())
        moduleFragment.accept(IrTransformer(context), null)
    }
}