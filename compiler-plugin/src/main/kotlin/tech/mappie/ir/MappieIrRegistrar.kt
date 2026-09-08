package tech.mappie.ir

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import tech.mappie.MappieContextFileManager
import tech.mappie.config.MappieConfiguration
import tech.mappie.ir.analysis.report
import tech.mappie.ir.generation.CodeGenerationStage
import tech.mappie.ir.generation.CodeModelGenerationStage
import tech.mappie.ir.postprocessing.PostProcessingStage
import tech.mappie.ir.preprocessing.PreprocessingStage
import tech.mappie.ir.resolving.ResolvingStage
import tech.mappie.ir.selection.SelectionStage

class MappieIrRegistrar(
    private val messageCollector: MessageCollector,
    private val configuration: MappieConfiguration,
) : IrGenerationExtension {

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val context = createMappieContext(pluginContext)

        val preprocessed = context(context) {
            PreprocessingStage.execute(moduleFragment)
        }

        context(context.copy(definitions = preprocessed.definitions)) {
            val resolved = ResolvingStage.execute(preprocessed.definitions.internal)
            val selected = SelectionStage.execute(resolved.requests)

            selected.mappings.forEach { (_, request) ->
                request.validation.problems.forEach { problem ->
                    context
                        .at(problem.location, problem.file)
                        .report(problem)
                }
            }

            val requests = selected.mappings
                .filter { it.value.request != null && it.value.validation.isValid }
                .mapValues { it.value.request!! }

            val models = CodeModelGenerationStage.execute(requests)
            val generated = CodeGenerationStage.execute(models.models)

            PostProcessingStage.execute(generated)
        }
    }

    private fun createMappieContext(pluginContext: IrPluginContext) = MappieContext(
        messageCollector,
        pluginContext,
        this@MappieIrRegistrar.configuration,
        MappieDefinitionCollection(),
        MappieContextFileManager.load(configuration.outputDir),
    )
}