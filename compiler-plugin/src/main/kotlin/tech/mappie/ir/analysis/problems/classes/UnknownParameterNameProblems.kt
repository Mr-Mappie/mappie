package tech.mappie.ir.analysis.problems.classes

import org.jetbrains.kotlin.ir.util.fileEntry
import org.jetbrains.kotlin.name.Name
import tech.mappie.ir.resolving.ClassMappingRequest
import tech.mappie.ir.resolving.classes.sources.ClassMappingSource
import tech.mappie.ir.resolving.classes.sources.ExplicitClassMappingSource
import tech.mappie.ir.util.location
import tech.mappie.ir.analysis.Problem
import tech.mappie.ir.analysis.ValidationContext

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
