package tech.mappie.ir.analysis.problems.classes

import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import tech.mappie.ir.analysis.MappingValidation
import tech.mappie.ir.analysis.Problem
import tech.mappie.ir.analysis.ValidationContext
import tech.mappie.ir.resolving.ClassMappingRequest
import tech.mappie.ir.resolving.MappingResolver
import tech.mappie.ir.resolving.ResolverContext
import tech.mappie.ir.resolving.classes.sources.*
import tech.mappie.ir.resolving.classes.targets.ClassMappingTarget
import tech.mappie.ir.util.location

class MapperGenerationRequestProblems(
    private val context: ValidationContext,
    private val generated: List<Pair<ClassMappingTarget, GeneratedViaMapperTransformation>>,
) {

    fun all(): List<Problem> = generated
        .filter { (_, transformation) ->
            context.generated.none { it.first == transformation.source.type && it.second == transformation.target.type }
        }.map { (_, transformation) ->
            val requests = MappingResolver.of(
                transformation.source.type,
                transformation.target.type,
                ResolverContext(context, context.definitions, context.function)
            )
                .resolve(null)

            val context = context.copy(generated = generated.map { it.second.source.type to it.second.target.type })
            return requests
                .associateBy { request -> MappingValidation.of(context, request) }
                .filter { (validation, _) -> !validation.isValid() }
                .map { (validation, request) ->
                    Problem.error(
                        "No implicit mapping can be generated from ${request.source.type.dumpKotlinLike()} to ${request.target.type.dumpKotlinLike()}",
                        location(context.function),
                        validation.problems.map { it.description }
                    )
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