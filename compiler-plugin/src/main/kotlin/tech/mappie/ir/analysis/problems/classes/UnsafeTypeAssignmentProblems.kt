package tech.mappie.ir.analysis.problems.classes

import org.jetbrains.kotlin.ir.util.*
import tech.mappie.ir.MappieContext
import tech.mappie.ir.analysis.MappieIrAnalysisProblems.MAPPIE_MULTIPLE_UNSAFE_TYPE_ASSIGNMENTS
import tech.mappie.ir.analysis.MappieIrAnalysisProblems.MAPPIE_SINGLE_UNSAFE_TYPE_ASSIGNMENT
import tech.mappie.ir.resolving.ClassMappingRequest
import tech.mappie.ir.resolving.classes.sources.*
import tech.mappie.ir.resolving.classes.targets.ClassMappingTarget
import tech.mappie.util.filterSingle
import tech.mappie.ir.analysis.Problem
import tech.mappie.ir.reporting.pretty
import tech.mappie.ir.resolving.TargetSourcesClassMappings
import tech.mappie.ir.util.isSubtypeOf

class UnsafeTypeAssignmentProblems(
    private val mapping: ClassMappingRequest,
    private val mappings: Map<ClassMappingTarget, ClassMappingSource>,
) {

    fun all(): List<Problem> = buildList {
        val (implicit, explicit) =
            mappings
            .mapNotNull { validate(it.key, it.value)?.let { v -> it to v } }
            .partition { it.first.value is ImplicitClassMappingSource }

        addAll(explicit.map { it.second })
        when (implicit.size) {
            0 -> Unit
            1 -> addAll(implicit.map { it.second })
            else -> {
                val messages = implicit.map { (_, problem) ->
                    "Target ${problem.first} cannot be assigned from ${problem.second}."
                }
                add(
                    Problem.Problem1(
                        MAPPIE_MULTIPLE_UNSAFE_TYPE_ASSIGNMENTS,
                        mapping.origin.referenceMapFunction(),
                        messages
                    )
                )
            }
        }
    }

    private fun validate(target: ClassMappingTarget, source: ClassMappingSource): Problem.Problem2<String, String>? {
        val targetTypeString = target.type.dumpKotlinLike()
        val sourceTypeString = source.type.dumpKotlinLike()
        val targetString = "${mapping.target.dumpKotlinLike()}::${target.name.asString()}"

        return when (source) {
            is ExplicitPropertyMappingSource -> {
                val via = if (source.transformation != null && source.transformation is PropertyMappingViaMapperTransformation) " via '${source.transformation.mapper.clazz.name.asString()}'" else ""

                Problem.Problem2(
                    MAPPIE_SINGLE_UNSAFE_TYPE_ASSIGNMENT,
                    mapping.origin.clazz.file,
                    source.reference,
                    "'$targetString' of type '$targetTypeString'",
                    "'${source.reference.pretty()}'$via of type '$sourceTypeString'"
                )
            }
            is ExpressionMappingSource -> {
                Problem.Problem2(
                    MAPPIE_SINGLE_UNSAFE_TYPE_ASSIGNMENT,
                    mapping.origin.clazz.file,
                    source.expression,
                    "'$targetString' of type '$targetTypeString'",
                    "expression of type '$sourceTypeString'"
                )
            }
            is ValueMappingSource -> {
                Problem.Problem2(
                    MAPPIE_SINGLE_UNSAFE_TYPE_ASSIGNMENT,
                    mapping.origin.clazz.file,
                    source.expression,
                    "'$targetString' of type '$targetTypeString'",
                    "value of type '$sourceTypeString'"
                )
            }
            is FunctionMappingSource -> {
                Problem.Problem2(
                    MAPPIE_SINGLE_UNSAFE_TYPE_ASSIGNMENT,
                    mapping.origin.referenceMapFunction(),
                    "'$targetString' of type '$targetTypeString'",
                    "'${source.parameterType.dumpKotlinLike()}::${source.function.name.asString()}' of type '$sourceTypeString'"
                )
            }
            is ImplicitPropertyMappingSource -> {
                val property = "${source.parameterType.dumpKotlinLike()}::${source.property.name.asString()}"
                val via = if (source.transformation != null && source.transformation is PropertyMappingViaMapperTransformation) " via '${source.transformation.mapper.clazz.name.asString()}'" else ""

                Problem.Problem2(
                    MAPPIE_SINGLE_UNSAFE_TYPE_ASSIGNMENT,
                    mapping.origin.referenceMapFunction(),
                    "'$targetString' of type '$targetTypeString'",
                    "'$property'$via of type '$sourceTypeString'"
                )
            }
            is ParameterValueMappingSource -> {
                Problem.Problem2(
                    MAPPIE_SINGLE_UNSAFE_TYPE_ASSIGNMENT,
                    mapping.origin.referenceMapFunction(),
                    "'$targetString' of type '$targetTypeString'",
                    "'${source.parameter.asString()}' of type '$sourceTypeString'"
                )
            }
            is ParameterDefaultValueMappingSource -> {
                null
            }
        }
    }

    companion object {
        context(context: MappieContext)
        fun of(mapping: ClassMappingRequest): UnsafeTypeAssignmentProblems {
            val mappings = (mapping.mappings as TargetSourcesClassMappings)
                .filterSingle()
                .filter { (target, source) ->
                    !source.type.isSubtypeOf(target.type)
                }

            return UnsafeTypeAssignmentProblems(mapping, mappings)
        }
    }
}