package tech.mappie.ir.analysis.problems.classes

import org.jetbrains.kotlin.build.joinToReadableString
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

class MultipleSourcesProblems(
    private val mapping: ClassMappingRequest,
    private val mappings: Map<ClassMappingTarget, List<ClassMappingSource>>
) {

    fun all(): List<Problem> = mappings.map { (target, sources) ->
        when {
            sources.isEmpty() -> {
                Problem.Problem1(
                    MappieIrAnalysisProblems.MAPPIE_NO_MAPPING_SOURCE,
                    mapping.origin.referenceMapFunction(),
                    "${mapping.target.dumpKotlinLike()}::${target.name.asString()}"
                )
            }
            else -> {
                val sourceNames = sources.mapNotNull { source ->
                    when (source) {
                        is ImplicitPropertyMappingSource -> "${source.parameter}::${source.property.name}"
                        is FunctionMappingSource -> "${source.parameter}::${source.function.name} "
                        is ParameterValueMappingSource -> source.parameter.asString()
                        else -> null
                    }
                }.distinct()

                Problem.Problem2(
                    MappieIrAnalysisProblems.MAPPIE_MULTIPLE_MAPPING_SOURCES,
                    mapping.origin.referenceMapFunction(),
                    "${mapping.target.dumpKotlinLike()}::${target.name.asString()}",
                    sourceNames.joinToReadableString()
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
