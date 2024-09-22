package tech.mappie.validation.problems.classes

import org.jetbrains.kotlin.name.Name
import tech.mappie.resolving.ClassMappingRequest
import tech.mappie.validation.Problem

class UnknownParameterNameProblems(private val unknowns: Set<Name>) {

    fun all(): List<Problem> = unknowns.map {
        Problem.error("Identifier ${it.asString()} does not occur as as setter or as a parameter in constructor")
    }

    companion object {
        fun of(mapping: ClassMappingRequest): UnknownParameterNameProblems =
            UnknownParameterNameProblems(mapping.unknowns.keys)
    }
}
