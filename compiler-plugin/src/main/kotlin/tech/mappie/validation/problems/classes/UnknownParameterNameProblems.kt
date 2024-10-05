package tech.mappie.validation.problems.classes

import org.jetbrains.kotlin.ir.util.fileEntry
import org.jetbrains.kotlin.name.Name
import tech.mappie.resolving.ClassMappingRequest
import tech.mappie.resolving.classes.sources.ClassMappingSource
import tech.mappie.resolving.classes.sources.ExplicitClassMappingSource
import tech.mappie.util.location
import tech.mappie.validation.Problem
import tech.mappie.validation.ValidationContext

class UnknownParameterNameProblems(
    private val context: ValidationContext,
    private val unknowns: Map<Name, List<ClassMappingSource>>,
) {

    fun all(): List<Problem> = unknowns.map { (name, sources) ->
        Problem.error(
            "Identifier ${name.asString()} does not occur as as setter or as a parameter in constructor",
            when (val source = sources.firstOrNull()) {
                is ExplicitClassMappingSource -> location(context.function.fileEntry, source.origin)
                else -> location(context.function)
            }
        )
    }

    companion object {
        fun of(context: ValidationContext, mapping: ClassMappingRequest): UnknownParameterNameProblems =
            UnknownParameterNameProblems(context, mapping.unknowns)
    }
}
