package tech.mappie.ir.analysis.problems.classes

import org.jetbrains.kotlin.ir.util.parentAsClass
import tech.mappie.ir.util.location
import tech.mappie.ir.analysis.Problem
import tech.mappie.ir.analysis.ValidationContext
import tech.mappie.ir.resolving.ClassUpdateRequest

class EmptyUpdateMappieProblems(private val context: ValidationContext, private val mapping: ClassUpdateRequest) {

    fun all(): List<Problem> =
        if (mapping.mappings.isEmpty()) {
            listOf(Problem.warning("Class does not update anything", location(context.function.parentAsClass)))
        } else {
            emptyList()
        }

    companion object {
        fun of(context: ValidationContext, mapping: ClassUpdateRequest): EmptyUpdateMappieProblems =
            EmptyUpdateMappieProblems(context, mapping)
    }
}