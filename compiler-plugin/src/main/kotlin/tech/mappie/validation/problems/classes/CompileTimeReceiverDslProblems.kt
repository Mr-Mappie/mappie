package tech.mappie.validation.problems.classes

import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.fileEntry
import tech.mappie.resolving.ClassMappingRequest
import tech.mappie.resolving.classes.sources.ValueMappingSource
import tech.mappie.util.CLASS_ID_OBJECT_MAPPING_CONSTRUCTOR
import tech.mappie.util.location
import tech.mappie.validation.Problem
import tech.mappie.validation.ValidationContext

class CompileTimeReceiverDslProblems private constructor(
    private val context: ValidationContext,
    private val mappings: List<Pair<IrCall, ProblemSource>>,
) {
    private enum class ProblemSource { EXTENSION, DISPATCH }

    fun all(): List<Problem> = mappings.map { (call, source) ->
        val name = call.symbol.owner.name.asString()
        Problem.error(
            when (source) {
                ProblemSource.EXTENSION -> "The function $name was called as an extension method on the mapping dsl which does not exist after compilation"
                ProblemSource.DISPATCH -> "The function $name was called on the mapping dsl which does not exist after compilation"
            },
            location(context.function.fileEntry, call),
        )
    }

    companion object {
        fun of(context: ValidationContext, mapping: ClassMappingRequest): CompileTimeReceiverDslProblems {
            val mappings = mapping.mappings.values
                .filter { it.size == 1 }
                .map { it.single() }
                .filterIsInstance<ValueMappingSource>()
                .map { it.expression }
                .filterIsInstance<IrCall>()

            val dispatch = mappings
                .filter { it.hasIncorrectDispatchReceiver(context) }
                .map { it to ProblemSource.DISPATCH }

            val extension = mappings
                .filter { it.hasIncorrectExtensionReceiver(context) }
                .map { it to ProblemSource.EXTENSION }

            return CompileTimeReceiverDslProblems(context, dispatch + extension)
        }

        private fun IrCall.hasIncorrectExtensionReceiver(context: ValidationContext) =
            extensionReceiver?.type?.classOrNull == context.pluginContext.referenceClass(CLASS_ID_OBJECT_MAPPING_CONSTRUCTOR)

        private fun IrCall.hasIncorrectDispatchReceiver(context: ValidationContext) =
            dispatchReceiver?.type?.classOrNull == context.pluginContext.referenceClass(CLASS_ID_OBJECT_MAPPING_CONSTRUCTOR)
    }
}