package tech.mappie.validation.problems.enums

import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.util.kotlinFqName
import tech.mappie.resolving.EnumMappingRequest
import tech.mappie.resolving.enums.ExplicitEnumMappingTarget
import tech.mappie.resolving.enums.ResolvedEnumMappingTarget
import tech.mappie.validation.Problem
import tech.mappie.validation.ValidationContext

class UnnecessaryExplicitMappingProblems(
    private val mappings: List<IrEnumEntry>,
) {

    fun all(): List<Problem> = mappings.map {
        val name = "${it.parent.kotlinFqName.shortName().asString()}.${it.name}"
        Problem.warning("Unnecessary explicit mapping of source $name")
    }

    companion object {
        fun of(context: ValidationContext, mapping: EnumMappingRequest): UnnecessaryExplicitMappingProblems {
            val mappings = mapping.mappings.entries.fold(mutableListOf<IrEnumEntry>()) { acc, (entry, targets) ->
                val hasResolved = targets.any { it is ResolvedEnumMappingTarget }
                val hasExplicit = targets.any { it is ExplicitEnumMappingTarget }

                acc.apply {
                    if (hasResolved && hasExplicit) {
                        add(entry)
                    }
                }
            }
            return UnnecessaryExplicitMappingProblems(mappings)
        }
    }
}