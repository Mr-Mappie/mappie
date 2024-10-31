package tech.mappie.ir.analysis.problems.classes

import tech.mappie.config.options.getUseStrictEnumsAnnotation
import tech.mappie.ir.analysis.Problem
import tech.mappie.ir.analysis.ValidationContext
import tech.mappie.ir.resolving.ClassMappingRequest
import tech.mappie.ir.util.location

class ClassConfigProblems(private val context: ValidationContext) {

    fun all(): List<Problem> =
        buildList {
            if (context.getUseStrictEnumsAnnotation(context.function) != null) {
                add(Problem.warning("Annotation @UseStrictEnums has no effect on children of ObjectMappie", location(context.function)))
            }
        }

    companion object {
        fun of(context: ValidationContext, mapping: ClassMappingRequest): ClassConfigProblems =
            ClassConfigProblems(context)
    }
}