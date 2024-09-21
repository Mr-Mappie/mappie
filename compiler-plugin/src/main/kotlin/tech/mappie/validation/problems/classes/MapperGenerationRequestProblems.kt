package tech.mappie.validation.problems.classes

import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import tech.mappie.exceptions.MappiePanicException
import tech.mappie.resolving.ClassMappingRequest
import tech.mappie.resolving.MappingResolver
import tech.mappie.resolving.ResolverContext
import tech.mappie.resolving.classes.sources.*
import tech.mappie.resolving.classes.targets.ClassMappingTarget
import tech.mappie.util.location
import tech.mappie.validation.MappingValidation
import tech.mappie.validation.Problem
import tech.mappie.validation.ValidationContext

class MapperGenerationRequestProblems(
    private val context: ValidationContext,
    private val generated: List<Pair<ClassMappingTarget, GeneratedViaMapperTransformation>>,
) {

    fun all(): List<Problem> = generated
        .filter { (_, transformation) ->
            context.generated.none { it.first == transformation.source.type && it.second == transformation.target.type }
        }.map { (_, transformation) ->
        val requests = MappingResolver.of(transformation.source.type, transformation.target.type, ResolverContext(context, context.definitions, context.function))
            .resolve(null)

        val context = context.copy(generated = generated.map { it.second.source.type to it.second.target.type })
        return requests
            .associateBy { request -> MappingValidation.of(context, request) }
            .mapNotNull { (validation, request) ->
                if (validation.isValid()) {
                    null
                } else {
                    Problem.error(
                        "No implicit mapping can be generated from ${request.source.type.dumpKotlinLike()} to ${request.target.type.dumpKotlinLike()}",
                        location(context.function),
                        validation.errors().map { it.description }
                    )
                }
            }
    }

    companion object {
        fun of(context: ValidationContext, mapping: ClassMappingRequest): MapperGenerationRequestProblems {
            val mappings = mapping.mappings
                .filter { (_, sources) -> sources.size == 1 }
                .mapValues { (_, sources) -> sources.single() }
                .filter { (_, source) -> source.hasGeneratedTransformationMapping() }
                .map { (target, source) -> target to source.selectGeneratedTransformationMapping() }

            return MapperGenerationRequestProblems(context, mappings)
        }
    }
}