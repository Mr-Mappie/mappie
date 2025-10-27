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
        }
        return ResolvingResult(requests)
    }
}

data class ResolvingResult(val requests: Map<InternalMappieDefinition, List<MappingRequest>>)