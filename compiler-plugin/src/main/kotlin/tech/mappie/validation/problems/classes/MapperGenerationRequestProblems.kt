package tech.mappie.validation.problems.classes

import tech.mappie.resolving.ClassMappingRequest
import tech.mappie.resolving.MappingResolver
import tech.mappie.resolving.ResolverContext
import tech.mappie.resolving.classes.sources.GeneratedViaMapperTransformation
import tech.mappie.resolving.classes.sources.ImplicitPropertyMappingSource
import tech.mappie.resolving.classes.targets.ClassMappingTarget
import tech.mappie.util.location
import tech.mappie.validation.MappingValidation
import tech.mappie.validation.Problem
import tech.mappie.validation.ValidationContext

// Validate that a mapping can be constructed here
// If it can be generated, a generation model will be constructed via ClassMappieCodeGenerationModelFactory
//


// TODO: Can contain duplicate types
class MapperGenerationRequestProblems(
    private val context: ValidationContext,
    private val generated: List<Pair<ClassMappingTarget, GeneratedViaMapperTransformation>>,
) {

    fun all(): List<Problem> = generated.map { (_, transformation) ->
        val requests = MappingResolver.of(transformation.source.type, transformation.target.type, ResolverContext(context, context.definitions, context.function))
            .resolve(null)

        return if (requests.none { request -> MappingValidation.of(context, request).isValid() }) {
            listOf(
                // TODO: create a nice error message
                Problem.error("No mapping can be generated", location(context.function))
            )
        } else {
            emptyList()
        }
    }

    companion object {
        fun of(context: ValidationContext, mapping: ClassMappingRequest): MapperGenerationRequestProblems {
            val mappings = mapping.mappings
                .filter { (_, sources) -> sources.size == 1 }
                .mapValues { (_, sources) -> sources.single() }
                .filter { (_, source) -> source is ImplicitPropertyMappingSource && source.transformation is GeneratedViaMapperTransformation }
                .map { (target, source) -> target to (source as ImplicitPropertyMappingSource).transformation as GeneratedViaMapperTransformation }

            return MapperGenerationRequestProblems(context, mappings)
        }
    }
}