package tech.mappie.ir.analysis.problems.classes

import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.util.fileEntry
import org.jetbrains.kotlin.ir.util.isNullable
import tech.mappie.ir.resolving.ClassMappingRequest
import tech.mappie.ir.resolving.classes.sources.ExplicitPropertyMappingSource
import tech.mappie.util.filterSingle
import tech.mappie.ir.util.location
import tech.mappie.ir.analysis.Problem

class UnnecessaryFromPropertyNotNullProblems(
    private val mapping: ClassMappingRequest,
    private val mappings: List<ExplicitPropertyMappingSource>
) {

    fun all(): List<Problem> = mappings.map {
        Problem.warning(
            "Unnecessary fromPropertyNotNull for non-nullable type ${it.getterType.dumpKotlinLike()}",
            location(mapping.origin.clazz.fileEntry, it.origin),
            listOf("Use fromProperty instead of fromPropertyNotNull")
        )
    }

    companion object {
        fun of(mapping: ClassMappingRequest): UnnecessaryFromPropertyNotNullProblems =
            UnnecessaryFromPropertyNotNullProblems(
                mapping,
                mapping.mappings.values
                    .filterSingle()
                    .filterIsInstance<ExplicitPropertyMappingSource>()
                    .filter { it.forceNonNull && !it.getterType.isNullable() }
            )
    }
}