package tech.mappie.ir.analysis.problems.classes

import org.jetbrains.kotlin.ir.types.IrTypeSystemContextImpl
import org.jetbrains.kotlin.ir.util.*
import tech.mappie.ir.resolving.classes.sources.*
import tech.mappie.ir.resolving.classes.targets.ClassMappingTarget
import tech.mappie.util.filterSingle
import tech.mappie.ir.util.hasFlexibleNullabilityAnnotation
import tech.mappie.ir.util.location
import tech.mappie.ir.analysis.Problem
import tech.mappie.ir.analysis.ValidationContext
import tech.mappie.ir.resolving.ClassRequest
import tech.mappie.ir.util.isList
import tech.mappie.ir.util.isSet

class UnsafeTypeAssignmentProblems(
    private val context: ValidationContext,
    private val mapping: ClassRequest,
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
                val description = "Target $targetString of type $targetTypeString cannot be assigned from ${source.reference.dumpKotlinLike()} ${via}of type $sourceTypeString"
                Problem.error(description, location(context.function.fileEntry, source.reference))
            }
            is ExpressionMappingSource -> {
                val description = "Target $targetString of type $targetTypeString cannot be assigned from expression of type $sourceTypeString"
                Problem.error(description, location(context.function.fileEntry, source.expression))
            }
            is ValueMappingSource -> {
                val description = "Target $targetString of type $targetTypeString cannot be assigned from value of type $sourceTypeString"
                Problem.error(description, location(context.function.fileEntry, source.expression))
            }
            is FunctionMappingSource -> {
                val function = "${source.parameterType.dumpKotlinLike()}::${source.function.name.asString()}"
                val description = "Target $targetString automatically resolved from $function but cannot assign source type $sourceTypeString to target type $targetTypeString"
                Problem.error(description, location(mapping.origin))
            }
            is ImplicitPropertyMappingSource -> {
                val property = "${source.parameterType.dumpKotlinLike()}::${source.property.name.asString()}"
                val via = if (source.transformation != null && source.transformation is PropertyMappingViaMapperTransformation) "via ${source.transformation.mapper.clazz.name.asString()} " else ""
                val description = "Target $targetString automatically resolved from $property ${via}but cannot assign source type $sourceTypeString to target type $targetTypeString"
                Problem.error(description, location(mapping.origin))
            }
            is ParameterValueMappingSource -> {
                val description = "Target $targetString automatically resolved parameter ${source.parameter.asString()} but cannot assign source type $sourceTypeString to target type $targetTypeString}"
                Problem.warning(description, location(context.function.fileEntry, mapping.origin))
            }
            is ParameterDefaultValueMappingSource -> {
                null
            }
        }
    }

    companion object {
        fun of(context: ValidationContext, mapping: ClassRequest): UnsafeTypeAssignmentProblems {
            val mappings = mapping.mappings.filterSingle().filter { (target, source) ->
                context.isNotCompatible(source, target)
            }

            return UnsafeTypeAssignmentProblems(
                context,
                mapping,
                mappings
            )
        }

        private fun ValidationContext.isNotCompatible(source: ClassMappingSource, target: ClassMappingTarget) =
            (!source.type.isSubtypeOf(target.type, IrTypeSystemContextImpl(pluginContext.irBuiltIns)) && !isCompatibleCollection(source, target))
                || ((source.type.isNullable() && !source.type.hasFlexibleNullabilityAnnotation()) && !target.type.isNullable())

        private fun isCompatibleCollection(source: ClassMappingSource, target: ClassMappingTarget): Boolean =
            source.type.isList() && target.type.isList() || source.type.isSet() && target.type.isSet()
    }
}