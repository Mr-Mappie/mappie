package tech.mappie.ir.analysis.problems.classes

import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import tech.mappie.ir.analysis.MappieIrAnalysisProblems
import tech.mappie.ir.resolving.ClassMappingRequest
import tech.mappie.ir.resolving.classes.sources.ClassMappingSource
import tech.mappie.ir.resolving.classes.sources.FunctionMappingSource
import tech.mappie.ir.resolving.classes.sources.ImplicitPropertyMappingSource
import tech.mappie.ir.resolving.classes.sources.ParameterValueMappingSource
import tech.mappie.ir.resolving.classes.targets.ClassMappingTarget
import tech.mappie.ir.analysis.Problem
import tech.mappie.ir.resolving.TargetSourcesClassMappings
import tech.mappie.util.joinToReadableQuotedString

class MultipleSourcesProblems(
    private val mapping: ClassMappingRequest,
    private val mappings: Map<ClassMappingTarget, List<ClassMappingSource>>
) {

    fun all(): List<Problem> = buildList {
        val (withoutTarget, multipleTargets) =
            mappings.entries.partition { it.value.isEmpty() }

        generateNoTargets(withoutTarget)
        generateMultipleTargets(multipleTargets)
    }

    private fun MutableList<Problem>.generateNoTargets(withoutTarget: List<Map.Entry<ClassMappingTarget, List<ClassMappingSource>>>) {
        if (withoutTarget.isNotEmpty()) {
            val names = withoutTarget.map { (target, _) ->
                "${mapping.target.dumpKotlinLike()}::${target.name.asString()}"
            }
            add(
                Problem.Problem1(
                    MappieIrAnalysisProblems.MAPPIE_NO_MAPPING_SOURCE,
                    mapping.origin.referenceMapFunction(),
                    names
                )
            )
        }
    }

    private fun MutableList<Problem>.generateMultipleTargets(multipleTargets: List<Map.Entry<ClassMappingTarget, List<ClassMappingSource>>>) {
        if (multipleTargets.isNotEmpty()) {
            val problems = multipleTargets.map { (target, sources) ->
                target to sources.mapNotNull { source ->
                    when (source) {
                        is ImplicitPropertyMappingSource -> "${source.parameter}::${source.property.name}"
                        is FunctionMappingSource -> "${source.parameter}::${source.function.name} "
                        is ParameterValueMappingSource -> source.parameter.asString()
                        else -> null
                    }
                }.distinct()
            }

            if (problems.size == 1) {
                val (target, sources) = problems.single()
                add(
                    Problem.Problem2(
                        MappieIrAnalysisProblems.MAPPIE_SINGLE_TARGET_HAS_MULTIPLE_MAPPING_SOURCES,
                        mapping.origin.referenceMapFunction(),
                        "${mapping.target.dumpKotlinLike()}::${target.name.asString()}",
                        sources
                    )
                )
            } else {
                val messages = problems.map { (target, sources) ->
                    "Target '${target.name.asString()}' has sources ${sources.joinToReadableQuotedString()} defined."
                }
                add(
                    Problem.Problem1(
                        MappieIrAnalysisProblems.MAPPIE_MULTIPLE_TARGETS_HAVE_MULTIPLE_MAPPING_SOURCES,
                        mapping.origin.referenceMapFunction(),
                        messages
                    )
                )
            }
        }
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
