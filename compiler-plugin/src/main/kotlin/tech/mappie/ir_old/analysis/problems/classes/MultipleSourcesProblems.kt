package tech.mappie.ir_old.analysis.problems.classes

import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import tech.mappie.ir_old.resolving.ClassMappingRequest
import tech.mappie.ir_old.resolving.classes.sources.ClassMappingSource
import tech.mappie.ir_old.resolving.classes.targets.ClassMappingTarget
import tech.mappie.ir_old.analysis.Problem

class MultipleSourcesProblems(
    private val targetType: IrType,
    private val mappings: Map<ClassMappingTarget, List<ClassMappingSource>>
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
                    .filter { (_, sources) -> sources.size != 1 }
            )
    }
}
