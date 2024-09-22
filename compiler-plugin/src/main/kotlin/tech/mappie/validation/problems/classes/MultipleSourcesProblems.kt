package tech.mappie.validation.problems.classes

import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import tech.mappie.resolving.ClassMappingRequest
import tech.mappie.resolving.classes.sources.ClassMappingSource
import tech.mappie.resolving.classes.targets.ClassMappingTarget
import tech.mappie.validation.Problem

class MultipleSourcesProblems(
    private val targetType: IrType,
    private val mappings: List<Pair<ClassMappingTarget, List<ClassMappingSource>>>
) {

    fun all(): List<Problem> = mappings.map { (target, sources) ->
        Problem.error("Target ${targetType.dumpKotlinLike()}::${target.name.asString()} has ${if (sources.isEmpty()) "no source defined" else "multiple sources defined"}")
    }

    companion object {
        fun of(mapping: ClassMappingRequest): MultipleSourcesProblems =
            MultipleSourcesProblems(
                mapping.target,
                mapping.mappings
                    .filter { (target, _) -> target.required }
                    .filter { (_, sources) -> sources.size != 1 }.map { it.toPair() }
            )
    }
}
