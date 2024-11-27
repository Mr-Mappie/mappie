package tech.mappie.validation.problems.classes

import org.jetbrains.kotlin.ir.types.isNullable
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.util.fileEntry
import tech.mappie.resolving.ClassMappingRequest
import tech.mappie.resolving.classes.sources.ExplicitPropertyMappingSource
import tech.mappie.util.filterSingle
import tech.mappie.util.location
import tech.mappie.validation.Problem
import tech.mappie.validation.ValidationContext

class UnnecessaryFromPropertyNotNullProblems(
    private val context: ValidationContext,
     private val mappings: List<ExplicitPropertyMappingSource>
) {
    fun all(): List<Problem> = mappings.map {
        Problem.warning(
            "Unnecessary fromPropertyNotNull for non-nullable type ${it.getterType.dumpKotlinLike()}",
            location(context.function.fileEntry, it.origin),
            listOf("Use fromProperty instead of fromPropertyNotNull")
        )
    }

    companion object {
        fun of(context: ValidationContext, mapping: ClassMappingRequest): UnnecessaryFromPropertyNotNullProblems =
            UnnecessaryFromPropertyNotNullProblems(
                context,
                mapping.mappings.values
                    .filterSingle()
                    .filterIsInstance<ExplicitPropertyMappingSource>()
                    .filter { it.forceNonNull && !it.getterType.isNullable() }
            )
    }
}