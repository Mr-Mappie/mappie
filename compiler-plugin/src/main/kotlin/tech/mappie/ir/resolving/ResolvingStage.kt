package tech.mappie.ir.resolving

import tech.mappie.ir.GeneratedMappieDefinition
import tech.mappie.ir.InternalMappieDefinition
import tech.mappie.ir.MappieContext
import tech.mappie.ir.MappieDefinition

/**
 * IR stage responsible for matching the sources and targets of all internal definitions.
 */
object ResolvingStage {

    context(context: MappieContext)
    fun execute(definitions: List<InternalMappieDefinition>): ResolvingResult {
        val requests = definitions.associateWith { definition ->
            definition.clazz.accept(MappingRequestResolver(definition), context)
        }.mapKeys { it.key as MappieDefinition }
        return ResolvingResult(requests)
    }

    context(context: MappieContext)
    fun execute(definition: GeneratedMappieDefinition): ResolvingResult {
        val requests = MappingResolverSelector.select(definition).resolve(definition.origin, null)
        return ResolvingResult(mapOf(definition to requests))
    }
}

data class ResolvingResult(val requests: Map<MappieDefinition, List<MappingRequest>>)