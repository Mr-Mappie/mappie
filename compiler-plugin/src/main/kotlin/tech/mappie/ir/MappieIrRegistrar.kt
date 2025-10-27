package tech.mappie.ir

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import tech.mappie.config.MappieConfiguration
import tech.mappie.ir.generation.CodeGenerationStage
import tech.mappie.ir.generation.CodeModelGenerationStage
import tech.mappie.ir.preprocessing.PreprocessingStage
import tech.mappie.ir.reporting.ReportGenerator
import tech.mappie.ir.resolving.MappieDefinitionCollection
import tech.mappie.ir.resolving.ResolvingStage
import tech.mappie.ir.selection.SelectionStage

class MappieIrRegistrar(
    private val messageCollector: MessageCollector,
    private val configuration: MappieConfiguration,
) : IrGenerationExtension {

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        context(createMappieContext(pluginContext)) {
            PreprocessingStage.execute(moduleFragment)
            val resolved = ResolvingStage.execute()
            val selected = SelectionStage.execute(resolved.requests)
            val models = CodeModelGenerationStage.execute(selected.mappings)
            val generated = CodeGenerationStage.execute(models.models)
            ReportGenerator().report(generated.classes)
        }
    }

    private fun createMappieContext(pluginContext: IrPluginContext) = object : MappieContext {
        override val pluginContext = pluginContext
        override val configuration = this@MappieIrRegistrar.configuration
        override val logger = MappieLogger(configuration.warningsAsErrors, messageCollector)
        override val definitions = MappieDefinitionCollection()
    }
}