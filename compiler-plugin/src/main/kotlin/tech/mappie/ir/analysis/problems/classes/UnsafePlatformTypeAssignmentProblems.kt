package tech.mappie.ir.analysis.problems.classes

import org.jetbrains.kotlin.ir.types.removeAnnotations
import org.jetbrains.kotlin.ir.util.isNullable
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.util.file
import tech.mappie.config.options.useStrictPlatformTypeNullabilityValidation
import tech.mappie.ir.MappieContext
import tech.mappie.ir.analysis.MappieIrAnalysisProblems.MAPPIE_UNSAFE_PLATFORM_TYPE_ASSIGNMENT
import tech.mappie.ir.resolving.ClassMappingRequest
import tech.mappie.ir.resolving.classes.sources.*
import tech.mappie.ir.resolving.classes.targets.ClassMappingTarget
import tech.mappie.util.filterSingle
import tech.mappie.ir.util.hasFlexibleNullabilityAnnotation
import tech.mappie.ir.analysis.Problem
import tech.mappie.ir.reporting.pretty
import tech.mappie.ir.resolving.TargetSourcesClassMappings

class UnsafePlatformTypeAssignmentProblems(
    private val mapping: ClassMappingRequest,
    private val mappings: Map<ClassMappingTarget, ClassMappingSource>,
) {

    context (context: MappieContext)
    fun all(): List<Problem> =
        if (useStrictPlatformTypeNullabilityValidation(mapping.origin.referenceMapFunction())) {
            mappings.mapNotNull { validate(it.key, it.value) }
        } else {
            emptyList()
        }

    private fun validate(target: ClassMappingTarget, source: ClassMappingSource): Problem? {
        val targetString = "${mapping.target.dumpKotlinLike()}::${target.name.asString()}"
        val sourceType = source.type.removeAnnotations().dumpKotlinLike()

        return when (source) {
            is ExplicitPropertyMappingSource -> {
                Problem.Problem2(
                    MAPPIE_UNSAFE_PLATFORM_TYPE_ASSIGNMENT,
                    mapping.origin.clazz.file,
                    source.reference,
                    "'$targetString' of type '${mapping.target.dumpKotlinLike()}'",
                    "'${source.reference.pretty()}' of type '$sourceType'",
                )
            }
            is ExpressionMappingSource -> {
                Problem.Problem2(
                    MAPPIE_UNSAFE_PLATFORM_TYPE_ASSIGNMENT,
                    mapping.origin.clazz.file,
                    source.expression,
                    "'${targetString}' of type '${mapping.target.dumpKotlinLike()}'",
                    "expression of type '$sourceType'",
                )
            }
            is ValueMappingSource -> {
                Problem.Problem2(
                    MAPPIE_UNSAFE_PLATFORM_TYPE_ASSIGNMENT,
                    mapping.origin.clazz.file,
                    source.expression,
                    "'${targetString}' of type '${mapping.target.dumpKotlinLike()}'",
                    "value '$sourceType'",
                )
            }
            is FunctionMappingSource -> {
                Problem.Problem2(
                    MAPPIE_UNSAFE_PLATFORM_TYPE_ASSIGNMENT,
                    mapping.origin.referenceMapFunction(),
                    "'${targetString}' of type '${mapping.target.dumpKotlinLike()}'",
                    "'${source.parameterType}::${source.function.name.asString()}' of type '$sourceType'",
                )
            }
            is ImplicitPropertyMappingSource -> {
                Problem.Problem2(
                    MAPPIE_UNSAFE_PLATFORM_TYPE_ASSIGNMENT,
                    mapping.origin.referenceMapFunction(),
                    "'${targetString}' of type '${mapping.target.dumpKotlinLike()}'",
                    "'${source.property.dumpKotlinLike()}' of type '$sourceType'",

                )
            }
            is ParameterValueMappingSource -> {
                Problem.Problem2(
                    MAPPIE_UNSAFE_PLATFORM_TYPE_ASSIGNMENT,
                    mapping.origin.referenceMapFunction(),
                    "'${targetString}' of type '${mapping.target.dumpKotlinLike()}'",
                    "'${source.parameter.asString()}' of type '${source.type.dumpKotlinLike()}'",
                )
            }
            is ParameterDefaultValueMappingSource -> {
                null
            }
        }
    }

    companion object {
        fun of(mapping: ClassMappingRequest): UnsafePlatformTypeAssignmentProblems {
            val mappings = (mapping.mappings as TargetSourcesClassMappings)
                .filterSingle()
                .filter { (target, source) ->
                    source.type.hasFlexibleNullabilityAnnotation() && !target.type.isNullable()
                }

            return UnsafePlatformTypeAssignmentProblems(
                mapping,
                mappings
            )
        }
    }
}
