package tech.mappie.validation.problems.enums

import tech.mappie.config.options.getUseDefaultArgumentsAnnotation
import tech.mappie.config.options.getUseStrictVisibilityAnnotation
import tech.mappie.resolving.EnumMappingRequest
import tech.mappie.util.location
import tech.mappie.validation.Problem
import tech.mappie.validation.ValidationContext

class EnumConfigProblems(private val context: ValidationContext) {

    fun all(): List<Problem> =
        buildList {
            if (context.getUseDefaultArgumentsAnnotation(context.function) != null) {
                add(Problem.warning("Annotation @UseDefaultArguments has no effect on children of EnumMappie", location(context.function)))
            }
            if (context.getUseStrictVisibilityAnnotation(context.function) != null) {
                add(Problem.warning("Annotation @UseStrictVisibility has no effect on children of EnumMappie", location(context.function)))
            }
        }

    companion object {
        fun of(context: ValidationContext, mapping: EnumMappingRequest): EnumConfigProblems =
            EnumConfigProblems(context)
    }
}