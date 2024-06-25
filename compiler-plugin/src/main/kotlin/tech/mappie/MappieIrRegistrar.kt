package tech.mappie

import tech.mappie.generation.IrTransformer
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

class MappieIrRegistrar(
    private val messageCollector: MessageCollector,
    private val configuration: MappieConfiguration,
) : IrGenerationExtension {

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        context = MappiePluginContext(messageCollector, configuration, pluginContext)
        moduleFragment.accept(IrTransformer(), null)
    }

    companion object {
        lateinit var context: MappiePluginContext
    }
}