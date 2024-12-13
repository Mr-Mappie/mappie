package tech.mappie.validation.problems.classes

import org.jetbrains.kotlin.ir.types.isNullable
import org.jetbrains.kotlin.ir.types.removeAnnotations
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.util.fileEntry
import tech.mappie.resolving.ClassMappingRequest
import tech.mappie.resolving.classes.sources.*
import tech.mappie.resolving.classes.targets.ClassMappingTarget
import tech.mappie.util.filterSingle
import tech.mappie.util.hasFlexibleNullabilityAnnotation
import tech.mappie.util.location
import tech.mappie.validation.Problem
import tech.mappie.validation.ValidationContext

class UnsafePlatformTypeAssignmentProblems(
    private val context: ValidationContext,
    private val mapping: ClassMappingRequest,
    private val mappings: Map<ClassMappingTarget, ClassMappingSource>,
) {

    fun all(): List<Problem> = mappings.mapNotNull { validate(it.key, it.value) }

    private fun validate(target: ClassMappingTarget, source: ClassMappingSource): Problem? {
        val sourceTypeString = source.type.removeAnnotations().dumpKotlinLike()
        val targetTypeString = mapping.target.dumpKotlinLike()
        val targetString = "$targetTypeString::${target.name.asString()}"

        return when (source) {
            is ExplicitPropertyMappingSource -> {
                val description = "Target $targetString of type $targetTypeString is unsafe to from ${source.reference.dumpKotlinLike()} of platform type $sourceTypeString"
                Problem.warning(description, location(context.function.fileEntry, source.reference))
            }
            is ExpressionMappingSource -> {
                val description = "Target $targetString of type $targetTypeString is unsafe to be assigned from expression of platform type $sourceTypeString"
                Problem.warning(description, location(context.function.fileEntry, source.expression))
            }
            is ValueMappingSource -> {
                val description = "Target $targetString of type $targetTypeString is unsafe to assigned from value of platform type $sourceTypeString"
                Problem.warning(description, location(context.function.fileEntry, source.expression))
            }
            is FunctionMappingSource -> {
                val function = "${source.parameterType}::${source.function.name.asString()}"
                val description = "Target $targetString automatically resolved from $function but it is unsafe to assign source platform type $sourceTypeString to target type $targetTypeString"
                Problem.warning(description, location(mapping.origin))

            }
            is ImplicitPropertyMappingSource -> {
                val description = "Target $targetString automatically resolved from ${source.property.dumpKotlinLike()} but it is unsafe to assign source platform type $sourceTypeString to target type ${target.type.dumpKotlinLike()}"
                Problem.warning(description, location(context.function.fileEntry, mapping.origin))
            }
            is ParameterValueMappingSource -> {
                val description = "Target $targetString automatically resolved parameter ${source.parameter.asString()} but it is unsafe to assign source platform type $sourceTypeString to target type ${target.type.dumpKotlinLike()}"
                Problem.warning(description, location(context.function.fileEntry, mapping.origin))
            }
            is ParameterDefaultValueMappingSource -> {
                null
            }
        }
    }

    companion object {
        fun of(context: ValidationContext, mapping: ClassMappingRequest): UnsafePlatformTypeAssignmentProblems {
            val mappings = mapping.mappings
                .filterSingle()
                .filter { (target, source) ->
                    source.type.hasFlexibleNullabilityAnnotation() && !target.type.isNullable()
                }

            return UnsafePlatformTypeAssignmentProblems(
                context,
                mapping,
                mappings
            )
        }
    }
}
