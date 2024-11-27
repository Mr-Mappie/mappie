package tech.mappie.validation.problems.classes

import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import tech.mappie.resolving.ClassMappingRequest
import tech.mappie.resolving.MappingResolver
import tech.mappie.resolving.ResolverContext
import tech.mappie.resolving.classes.sources.*
import tech.mappie.util.filterSingle
import tech.mappie.util.location
import tech.mappie.util.mappieType
import tech.mappie.validation.MappingValidation
import tech.mappie.validation.Problem
import tech.mappie.validation.ValidationContext

class MapperGenerationRequestProblems(
    private val context: ValidationContext,
    private val requests: List<GeneratedViaMapperTransformation>,
) {

    fun all(): List<Problem> = requests
        .filter { transformation -> isDuplicate(transformation) }
        .map { transformation ->
            val source = transformation.source.type.mappieType()
            val target = transformation.target.type.mappieType()
            val requests = MappingResolver.of(source, target, ResolverContext(context, context.definitions, context.function))
                .resolve(null)

            return if (requests.isEmpty()) {
                listOf(Problem.error(
                    "No implicit mapping can be generated from ${source.dumpKotlinLike()} to ${target.dumpKotlinLike()}",
                    location(context.function),
                    listOf("Target class has no accessible constructor"),
                ))
            } else {
                val context = context.copy(generated = requests.map { it.source.type to it.target.type })
                requests
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
        }

    private fun isDuplicate(transformation: GeneratedViaMapperTransformation): Boolean =
        context.generated.none {
            it.first == transformation.source.type.mappieType() && it.second == transformation.target.type.mappieType()
        }

    companion object {
        fun of(context: ValidationContext, mapping: ClassMappingRequest): MapperGenerationRequestProblems {
            val mappings = mapping.mappings.values
                .filterSingle()
                .filter { it.hasGeneratedTransformationMapping() }
                .map { it.selectGeneratedTransformationMapping() }

            return MapperGenerationRequestProblems(context, mappings)
        }
    }
}