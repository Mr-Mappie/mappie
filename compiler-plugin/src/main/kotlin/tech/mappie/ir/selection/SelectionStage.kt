package tech.mappie.ir.selection

import tech.mappie.ir.MappieContext
import tech.mappie.ir.analysis.Problem
import tech.mappie.ir.analysis.Problem.Companion.error
import tech.mappie.ir.analysis.RequestValidator
import tech.mappie.ir.resolving.InternalMappieDefinition
import tech.mappie.ir.resolving.MappingRequest
import tech.mappie.ir.util.location

/**
 * IR stage responsible for matching the sources and targets of all internal definitions.
 */
object SelectionStage {

    context(context: MappieContext)
    fun execute(requests: Map<InternalMappieDefinition, List<MappingRequest>>): SelectionResult {
        val mappings: Map<InternalMappieDefinition, MappingRequest> = requests.mapNotNull { (definition, mappings) ->
            val validations = mappings.associateWith { RequestValidator.of(it).evaluate() }
            val selected = MappingSelector.of(validations).select()

            if (selected == null) {
                context.logger.log(error("Target class has no accessible constructor", location(definition.clazz)))
                null
            } else {
                val request = selected.first

                context.logger.logAll(selected.second.problems)

                if (request == null) {
                    null
                } else {
                    definition to request
                }
            }
        }.toMap()

        return SelectionResult(mappings)
    }
}

class SelectionResult(val mappings: Map<InternalMappieDefinition, MappingRequest>)