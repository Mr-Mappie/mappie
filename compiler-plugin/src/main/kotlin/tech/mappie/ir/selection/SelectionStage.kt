package tech.mappie.ir.selection

import tech.mappie.ir.MappieContext
import tech.mappie.ir.analysis.Problem.Companion.error
import tech.mappie.ir.analysis.RequestValidator
import tech.mappie.ir.analysis.ValidationResult
import tech.mappie.ir.MappieDefinition
import tech.mappie.ir.resolving.MappingRequest
import tech.mappie.ir.util.location

/**
 * IR stage responsible for matching the sources and targets of all internal definitions.
 */
object SelectionStage {

    context(context: MappieContext)
    fun execute(requests: Map<MappieDefinition, List<MappingRequest>>): SelectionResult {
        val mappings = requests.map { (definition, mappings) ->
            val validations = mappings.associateWith { RequestValidator.of(it).evaluate() }
            val selected = MappingSelector.of(validations).select()

            if (selected == null) {
                definition to MappingRequestProblemDecorator(null, ValidationResult(listOf(error("Target class has no accessible constructor", location(definition.origin.clazz)))))
            } else {
                definition to MappingRequestProblemDecorator(selected.first, selected.second)
            }
        }.toMap()

        return SelectionResult(mappings)
    }
}

data class MappingRequestProblemDecorator(val request: MappingRequest?, val validation: ValidationResult)

class SelectionResult(val mappings: Map<MappieDefinition, MappingRequestProblemDecorator>)