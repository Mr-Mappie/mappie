package tech.mappie.validation.problems

import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import tech.mappie.resolving.ConstructorCallMapping
import tech.mappie.resolving.classes.targets.MappieTarget
import tech.mappie.resolving.classes.ObjectMappingSource
import tech.mappie.validation.Problem

class MultipleSourcesProblems(
    private val targetType: IrType,
    private val mappings: List<Pair<MappieTarget, List<ObjectMappingSource>>>
) {

    fun all(): List<Problem> = mappings.map { (target, sources) ->
        Problem.error("Target ${targetType.dumpKotlinLike()}::${target.name.asString()} has ${if (sources.isEmpty()) "no source defined" else "multiple sources defined"}")
    }

    companion object {
        fun of(mapping: ConstructorCallMapping): MultipleSourcesProblems =
            MultipleSourcesProblems(
                mapping.targetType,
                mapping.mappings.filter { (_, sources) -> sources.size != 1 }.map { it.toPair() }
            )
    }
}
