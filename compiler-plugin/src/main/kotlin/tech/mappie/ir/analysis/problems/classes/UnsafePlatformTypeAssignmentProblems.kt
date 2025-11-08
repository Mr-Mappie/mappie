package tech.mappie.ir.analysis.problems.classes

import org.jetbrains.kotlin.ir.types.removeAnnotations
import org.jetbrains.kotlin.ir.util.isNullable
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.util.fileEntry
import tech.mappie.config.options.useStrictPlatformTypeNullabilityValidation
import tech.mappie.ir.resolving.ClassMappingRequest
import tech.mappie.ir.resolving.classes.sources.*
import tech.mappie.ir.resolving.classes.targets.ClassMappingTarget
import tech.mappie.util.filterSingle
import tech.mappie.ir.util.hasFlexibleNullabilityAnnotation
import tech.mappie.ir.util.location
import tech.mappie.ir.analysis.Problem
import tech.mappie.ir.reporting.pretty

class UnsafePlatformTypeAssignmentProblems(
    private val mapping: ClassMappingRequest,
    private val mappings: Map<ClassMappingTarget, ClassMappingSource>,
) {

    fun all(): List<Problem> =
        if (context.useStrictPlatformTypeNullabilityValidation(context.function)) {
            mappings.mapNotNull { validate(it.key, it.value) }
        } else {
            emptyList()
        }

    private fun validate(target: ClassMappingTarget, source: ClassMappingSource): Problem? {
        val sourceTypeString = source.type.removeAnnotations().dumpKotlinLike()
        val targetTypeString = mapping.target.dumpKotlinLike()
        val targetString = "$targetTypeString::${target.name.asString()}"

        return when (source) {
            is ExplicitPropertyMappingSource -> {
                val description = "Target $targetString of type $targetTypeString is unsafe to assign from ${source.reference.pretty()} of platform type $sourceTypeString"
                Problem.warning(description, location(mapping.origin.clazz.fileEntry, source.reference))
            }
            is ExpressionMappingSource -> {
                val description = "Target $targetString of type $targetTypeString is unsafe to be assigned from expression of platform type $sourceTypeString"
                Problem.warning(description, location(mapping.origin.clazz.fileEntry, source.expression))
            }
            is ValueMappingSource -> {
                val description = "Target $targetString of type $targetTypeString is unsafe to be assigned from value of platform type $sourceTypeString"
                Problem.warning(description, location(mapping.origin.clazz.fileEntry, source.expression))
            }
            is FunctionMappingSource -> {
                val function = "${source.parameterType}::${source.function.name.asString()}"
                val description = "Target $targetString automatically resolved from $function but it is unsafe to assign source platform type $sourceTypeString to target type $targetTypeString"
                Problem.warning(description, location(mapping.origin.clazz))
            }
            is ImplicitPropertyMappingSource -> {
                val description = "Target $targetString automatically resolved from ${source.property.dumpKotlinLike()} but it is unsafe to assign source platform type $sourceTypeString to target type ${target.type.dumpKotlinLike()}"
                Problem.warning(description, location(mapping.origin.clazz))
            }
            is ParameterValueMappingSource -> {
                val description = "Target $targetString automatically resolved parameter ${source.parameter.asString()} but it is unsafe to assign source platform type $sourceTypeString to target type ${target.type.dumpKotlinLike()}"
                Problem.warning(description, location(mapping.origin.clazz))
            }
            is ParameterDefaultValueMappingSource -> {
                null
            }
        }
    }

    companion object {
        fun of(mapping: ClassMappingRequest): UnsafePlatformTypeAssignmentProblems {
            val mappings = mapping.mappings
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
