package tech.mappie.ir.analysis.problems.classes

import org.jetbrains.kotlin.ir.util.file
import org.jetbrains.kotlin.ir.util.isNullable
import tech.mappie.ir.analysis.MappieIrAnalysisProblems.MAPPIE_UNNECESSARY_SAFE_CALL
import tech.mappie.ir.resolving.ClassMappingRequest
import tech.mappie.ir.resolving.classes.sources.ExplicitPropertyMappingSource
import tech.mappie.util.filterSingle
import tech.mappie.ir.analysis.Problem
import tech.mappie.ir.resolving.TargetSourcesClassMappings

class UnnecessaryFromPropertyNotNullProblems(
    private val mapping: ClassMappingRequest,
    private val mappings: List<ExplicitPropertyMappingSource>
) {

    fun all(): List<Problem> = mappings.map {
        Problem.Problem1(
            MAPPIE_UNNECESSARY_SAFE_CALL,
            mapping.origin.clazz.file,
            it.origin,
            it.source
        )
    }

    companion object {
        fun of(mapping: ClassMappingRequest): UnnecessaryFromPropertyNotNullProblems =
            UnnecessaryFromPropertyNotNullProblems(
                mapping,
                (mapping.mappings as TargetSourcesClassMappings)
                    .values
                    .filterSingle()
                    .filterIsInstance<ExplicitPropertyMappingSource>()
                    .filter { it.forceNonNull && !it.source.isNullable() }
            )
    }
}