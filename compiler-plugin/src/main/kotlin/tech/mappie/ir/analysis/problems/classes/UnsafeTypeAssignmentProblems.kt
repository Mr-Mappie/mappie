package tech.mappie.ir.analysis.problems.classes

import org.jetbrains.kotlin.ir.util.*
import tech.mappie.ir.MappieContext
import tech.mappie.ir.resolving.ClassMappingRequest
import tech.mappie.ir.resolving.classes.sources.*
import tech.mappie.ir.resolving.classes.targets.ClassMappingTarget
import tech.mappie.util.filterSingle
import tech.mappie.ir.util.location
import tech.mappie.ir.analysis.Problem
import tech.mappie.ir.reporting.pretty
import tech.mappie.ir.resolving.TargetSourcesClassMappings
import tech.mappie.ir.util.isSubtypeOf

class UnsafeTypeAssignmentProblems(
    private val mapping: ClassMappingRequest,
    private val mappings: Map<ClassMappingTarget, ClassMappingSource>,
) {

    fun all(): List<Problem> = mappings.mapNotNull { validate(it.key, it.value) }

    private fun validate(target: ClassMappingTarget, source: ClassMappingSource): Problem? {
        val targetTypeString = target.type.dumpKotlinLike()
        val sourceTypeString = source.type.dumpKotlinLike()
        val targetString = "${mapping.target.dumpKotlinLike()}::${target.name.asString()}"

        return when (source) {
            is ExplicitPropertyMappingSource -> {
                val via = if (source.transformation != null && source.transformation is PropertyMappingViaMapperTransformation) "via ${source.transformation.mapper.clazz.name.asString()} " else ""
                val description = "Target $targetString of type $targetTypeString cannot be assigned from ${source.reference.pretty()} ${via}of type $sourceTypeString"
                Problem.error(description, location(mapping.origin.clazz.fileEntry, source.reference))
            }
            is ExpressionMappingSource -> {
                val description = "Target $targetString of type $targetTypeString cannot be assigned from expression of type $sourceTypeString"
                Problem.error(description, location(mapping.origin.clazz.fileEntry, source.expression))
            }
            is ValueMappingSource -> {
                val description = "Target $targetString of type $targetTypeString cannot be assigned from value of type $sourceTypeString"
                Problem.error(description, location(mapping.origin.clazz.fileEntry, source.expression))
            }
            is FunctionMappingSource -> {
                val function = "${source.parameterType.dumpKotlinLike()}::${source.function.name.asString()}"
                val description = "Target $targetString automatically resolved from $function but cannot assign source type $sourceTypeString to target type $targetTypeString"
                Problem.error(description, location(mapping.origin.referenceMapFunction()))
            }
            is ImplicitPropertyMappingSource -> {
                val property = "${source.parameterType.dumpKotlinLike()}::${source.property.name.asString()}"
                val via = if (source.transformation != null && source.transformation is PropertyMappingViaMapperTransformation) "via ${source.transformation.mapper.clazz.name.asString()} " else ""
                val description = "Target $targetString automatically resolved from $property ${via}but cannot assign source type $sourceTypeString to target type $targetTypeString"
                Problem.error(description, location(mapping.origin.referenceMapFunction()))
            }
            is ParameterValueMappingSource -> {
                val description = "Target $targetString automatically resolved parameter ${source.parameter.asString()} but cannot assign source type $sourceTypeString to target type $targetTypeString"
                Problem.warning(description, location(mapping.origin.referenceMapFunction()))
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