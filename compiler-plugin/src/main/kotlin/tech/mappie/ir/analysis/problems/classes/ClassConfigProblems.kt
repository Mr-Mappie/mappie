package tech.mappie.validation.problems.classes

import tech.mappie.config.options.getUseStrictEnumsAnnotation
import tech.mappie.resolving.ClassMappingRequest
import tech.mappie.util.location
import tech.mappie.validation.Problem
import tech.mappie.validation.ValidationContext

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