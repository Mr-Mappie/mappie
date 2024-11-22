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
            val mappings = mapping.mappings
                .map { it.value }
                .filter { it.size == 1 }
                .map { it.single() }
                .filter { it.hasGeneratedTransformationMapping() }
                .map { it.selectGeneratedTransformationMapping() }

            return MapperGenerationRequestProblems(context, mappings)
        }
    }
}