package tech.mappie

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import tech.mappie.config.MappieConfiguration
import tech.mappie.exceptions.MappieProblemException
import tech.mappie.generation.CodeGenerationContext
import tech.mappie.generation.CodeGenerationModelFactory
import tech.mappie.generation.MappieCodeGenerator
import tech.mappie.preprocessing.DefinitionsCollector
import tech.mappie.resolving.*
import tech.mappie.selection.MappingSelector
import tech.mappie.util.isMappieMapFunction
import tech.mappie.util.location
import tech.mappie.validation.MappingValidation
import tech.mappie.validation.Problem
import tech.mappie.validation.ValidationContext

class MappieIrRegistrar(
    private val messageCollector: MessageCollector,
    private val configuration: MappieConfiguration,
) : IrGenerationExtension {

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        context = pluginContext

        handleMappieProblems {
            val context = DefinitionsCollector(createMappieContext(pluginContext)).collect(moduleFragment)
            val requests = moduleFragment.accept(MappingRequestResolver(), context)

            requests.forEach { (clazz, options) ->
                val selected = MappingSelector.of(options.associateWith {
                    MappingValidation.of(ValidationContext(context, context.definitions, emptyList(), it.origin), it)
                }).select()

                selected?.let { (solution, validation) ->
                    val function = clazz.declarations
                        .filterIsInstance<IrSimpleFunction>()
                        .first { it.isMappieMapFunction() }

                    context.logger.logAll(validation.problems, location(function))
                    if (solution != null) {
                        val model = CodeGenerationModelFactory.of(solution).construct(function)
                        clazz.accept(MappieCodeGenerator(CodeGenerationContext(context, model, context.definitions, emptyMap())), null)
                    }
                } ?: context.logger.log(Problem.error("Target class has no accessible constructor", location(clazz)))
            }
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