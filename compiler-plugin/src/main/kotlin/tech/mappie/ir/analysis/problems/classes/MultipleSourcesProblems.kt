package tech.mappie.ir.analysis.problems.classes

import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import tech.mappie.ir.resolving.ClassMappingRequest
import tech.mappie.ir.resolving.classes.sources.ClassMappingSource
import tech.mappie.ir.resolving.classes.targets.ClassMappingTarget
import tech.mappie.ir.analysis.Problem
import tech.mappie.ir.analysis.Problem.Companion.error
import tech.mappie.ir.resolving.TargetSourcesClassMappings
import tech.mappie.ir.util.location

class MultipleSourcesProblems(
    private val mapping: ClassMappingRequest,
    private val mappings: Map<ClassMappingTarget, List<ClassMappingSource>>
) {

    fun all(): List<Problem> = mappings.map { (target, sources) ->
        error("Target ${mapping.target.dumpKotlinLike()}::${target.name.asString()} has ${if (sources.isEmpty()) "no source defined" else "multiple sources defined"}", location(mapping.origin.referenceMapFunction()))
    }

    companion object {
        fun of(mapping: ClassMappingRequest): MultipleSourcesProblems =
            MultipleSourcesProblems(
                mapping,
                (mapping.mappings as TargetSourcesClassMappings)
                    .filter { (target, _) -> target.required }
                    .filter { (_, sources) -> sources.size != 1 }
            )
    }
}
