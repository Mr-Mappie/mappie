package tech.mappie

import tech.mappie.generation.IrTransformer
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import tech.mappie.resolving.AllMappieDefinitionsCollector

class MappieIrRegistrar(
    private val messageCollector: MessageCollector,
    private val configuration: MappieConfiguration,
) : IrGenerationExtension {

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        context = MappiePluginContext(messageCollector, configuration, pluginContext)
        val symbols = moduleFragment.accept(AllMappieDefinitionsCollector(), Unit)
        moduleFragment.accept(IrTransformer(symbols), null)
    }

    companion object {
        lateinit var context: MappiePluginContext
    }
}