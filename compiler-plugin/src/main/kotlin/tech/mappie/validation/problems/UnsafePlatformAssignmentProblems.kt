package tech.mappie.validation.problems

import org.jetbrains.kotlin.ir.IrFileEntry
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.removeAnnotations
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import tech.mappie.resolving.ConstructorCallMapping
import tech.mappie.resolving.classes.*
import tech.mappie.util.isAssignableFrom
import tech.mappie.util.location
import tech.mappie.validation.Problem

class UnsafePlatformAssignmentProblems(
    private val file: IrFileEntry,
    private val targetType: IrType,
    private val mappings: List<Pair<IrValueParameter, ObjectMappingSource>>,
) {

    fun all(): List<Problem> = mappings.map { warning(it.first, it.second) }

    private fun warning(target: IrValueParameter, source: ObjectMappingSource): Problem {
        val sourceTypeString = source.type.removeAnnotations().dumpKotlinLike()
        val targetTypeString = targetType.dumpKotlinLike()
        val targetString = "$targetTypeString::${target.name.asString()}"

        return when (source) {
            is PropertySource -> {
                val location = location(file, source.origin)
                val description = "Target $targetString of type $targetTypeString is unsafe to from ${source.property.dumpKotlinLike()} of platform type $sourceTypeString"
                Problem.warning(description, location)
            }
            is ExpressionSource -> {
                val location = location(file, source.origin)
                val description = "Target $targetString of type $targetTypeString is unsafe to be assigned from expression of platform type $sourceTypeString"
                Problem.warning(description, location)
            }
            is ResolvedSource -> {
                val description = "Target $targetString automatically resolved from ${source.property.dumpKotlinLike()} but it is unsafe to assign source platform type $sourceTypeString to target type ${target.type.dumpKotlinLike()}"
                Problem.warning(description, null)
            }
            is ValueSource -> {
                val location = source.origin?.let { location(file, it) }
                val description = "Target $targetString of type $targetTypeString is unsafe to assigned from value of platform type $sourceTypeString"
                Problem.warning(description, location)
            }
        }
    }

    companion object {
        fun of(file: IrFileEntry, mapping: ConstructorCallMapping): UnsafePlatformAssignmentProblems {
            val mappings = mapping.mappings
                .filter { (_, sources) -> sources.size == 1 }
                .filter { (target, sources) ->
                    target.type.isAssignableFrom(sources.single().type, true) &&
                            !target.type.isAssignableFrom(sources.single().type, false)
                }
                .map { (target, sources) -> target to sources.single() }

            return UnsafePlatformAssignmentProblems(
                file,
                mapping.targetType,
                mappings
            )
        }
    }
}

class UnsafeTypeAssignmentProblems(
    private val file: IrFileEntry,
    private val targetType: IrType,
    private val mappings: List<Pair<IrValueParameter, ObjectMappingSource>>,
) {

    fun all(): List<Problem> = mappings.map { warning(it.first, it.second) }

    private fun warning(target: IrValueParameter, source: ObjectMappingSource): Problem {
        val sourceTypeString = source.type.dumpKotlinLike()
        val targetTypeString = target.type.dumpKotlinLike()
        val targetString = "${targetType.dumpKotlinLike()}::${target.name.asString()}"

        return when (source) {
            is PropertySource -> {
                val location = location(file, source.origin)
                val description = "Target $targetString of type $targetTypeString cannot be assigned from ${source.property.dumpKotlinLike()} of type $sourceTypeString"
                Problem.error(description, location)
            }
            is ExpressionSource -> {
                val location = location(file, source.origin)
                val description = "Target $targetString of type $targetTypeString cannot be assigned from expression of type $sourceTypeString"
                Problem.error(description, location)
            }
            is ResolvedSource -> {
                val description = "Target $targetString automatically resolved from ${source.property.dumpKotlinLike()} but cannot assign source type $sourceTypeString to target type $targetTypeString"
                Problem.error(description, null)
            }
            is ValueSource -> {
                val location = source.origin?.let { location(file, it) }
                val description = "Target $targetString of type $targetTypeString cannot be assigned from value of type $sourceTypeString"
                Problem.error(description, location)
            }
        }
    }


    companion object {
        fun of(file: IrFileEntry, mapping: ConstructorCallMapping): UnsafeTypeAssignmentProblems {
            val mappings = mapping.mappings
                .filter { (_, sources) -> sources.size == 1 }
                .filter { (target, sources) ->
                    !target.type.isAssignableFrom(sources.single().type, true)
                }
                .map { (target, sources) -> target to sources.single() }

            return UnsafeTypeAssignmentProblems(
                file,
                mapping.targetType,
                mappings
            )
        }
    }
}