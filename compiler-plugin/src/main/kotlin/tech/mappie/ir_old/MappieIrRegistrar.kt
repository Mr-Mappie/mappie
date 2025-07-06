package tech.mappie.ir_old

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import tech.mappie.MappieContext
import tech.mappie.config.MappieConfiguration
import tech.mappie.exceptions.MappieProblemException
import tech.mappie.ir_old.generation.CodeGenerationContext
import tech.mappie.ir_old.generation.CodeGenerationModelFactory
import tech.mappie.ir_old.generation.MappieCodeGenerator
import tech.mappie.ir_old.preprocessing.DefinitionsCollector
import tech.mappie.ir_old.selection.MappingSelector
import tech.mappie.ir_old.util.isMappieMapFunction
import tech.mappie.ir_old.util.location
import tech.mappie.ir_old.analysis.MappingValidation
import tech.mappie.ir_old.analysis.Problem
import tech.mappie.ir_old.analysis.ValidationContext
import tech.mappie.ir_old.reporting.ReportGenerator
import tech.mappie.ir_old.resolving.MappingRequestResolver
import tech.mappie.ir_old.resolving.RequestResolverContext

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

                selected?.let { (solution, validation) ->
                    val function = clazz.declarations
                        .filterIsInstance<IrSimpleFunction>()
                        .first { it.isMappieMapFunction() }

                    context.logger.logAll(validation.problems, location(function))

                    solution?.let {
                        val model = CodeGenerationModelFactory.of(it).construct(function)
                        clazz.accept(MappieCodeGenerator(CodeGenerationContext(context, model, context.definitions, emptyMap())), null)
                    }
                } ?: context.logger.log(Problem.error("Target class has no accessible constructor", location(clazz))).let { null }
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