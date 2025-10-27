package tech.mappie.ir.resolving

import tech.mappie.ir.MappieContext

/**
 * IR stage responsible for matching the sources and targets of all internal definitions.
 */
object ResolvingStage {

    context(context: MappieContext)
    fun execute(): ResolvingResult {
        val requests = context.definitions.internal.associateWith { definition ->
            definition.clazz.accept(MappingRequestResolver(definition), context)
        }.mapKeys { it.key as MappieDefinition }
        return ResolvingResult(requests)
    }

    context(context: MappieContext)
    fun execute(origin: InternalMappieDefinition, definition: GeneratedMappieDefinition): ResolvingResult {
        val requests = MappingResolver.of(definition.source, definition.target).resolve(origin, null)
        return ResolvingResult(mapOf(definition to requests))
    }
}

data class ResolvingResult(val requests: Map<MappieDefinition, List<MappingRequest>>)