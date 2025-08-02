package tech.mappie.ir

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import tech.mappie.MappieContext
import tech.mappie.config.MappieConfiguration
import tech.mappie.exceptions.MappieProblemException
import tech.mappie.ir.generation.CodeGenerationContext
import tech.mappie.ir.generation.CodeGenerationModelFactory
import tech.mappie.ir.generation.MappieCodeGenerator
import tech.mappie.ir.preprocessing.DefinitionsCollector
import tech.mappie.ir.selection.MappingSelector
import tech.mappie.ir.util.isMappieMapFunction
import tech.mappie.ir.util.location
import tech.mappie.ir.analysis.MappingValidation
import tech.mappie.ir.analysis.Problem
import tech.mappie.ir.analysis.ValidationContext
import tech.mappie.ir.reporting.ReportGenerator
import tech.mappie.ir.resolving.MappingRequestResolver
import tech.mappie.ir.resolving.RequestResolverContext

class MappieIrRegistrar(
    private val messageCollector: MessageCollector,
    private val configuration: MappieConfiguration,
) : IrGenerationExtension {

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        context = pluginContext

        handleMappieProblems {
            val context = DefinitionsCollector(createMappieContext(pluginContext)).collect(moduleFragment)
            val requests = moduleFragment.accept(MappingRequestResolver(), RequestResolverContext(context, context.definitions))

            val generated = requests.mapNotNull { (clazz, options) ->
                val selected = MappingSelector.of(options.associateWith {
                    MappingValidation.of(ValidationContext(context, context.definitions, emptyList(), it.origin), it)
                }).select()

                if (selected != null) {
                    val (solution, validation) = selected
                    val function = clazz.declarations
                        .filterIsInstance<IrSimpleFunction>()
                        .first { it.isMappieMapFunction() }

                    context.logger.logAll(validation.problems, location(function))

                    if (solution != null) {
                        val model = CodeGenerationModelFactory.of(solution).construct(function)
                        clazz.accept(MappieCodeGenerator(CodeGenerationContext(context, model, context.definitions, emptyMap())), null)
                    } else {
                        null
                    }
                } else {
                    context.logger.log(Problem.error("Target class has no accessible constructor", location(clazz)))
                    null
                }
            }

            ReportGenerator(context).report(generated)
        }
    }

    private fun handleMappieProblems(function: () -> Unit): Unit =
        runCatching { function() }.getOrElse { if (it is MappieProblemException) Unit else throw it }

    private fun createMappieContext(pluginContext: IrPluginContext) = object : MappieContext {
        override val pluginContext = pluginContext
        override val configuration = this@MappieIrRegistrar.configuration
        override val logger = MappieLogger(configuration.warningsAsErrors, messageCollector)
    }

    companion object {
        lateinit var context: IrPluginContext
    }
}