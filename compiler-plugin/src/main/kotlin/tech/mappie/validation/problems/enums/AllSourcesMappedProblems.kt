package tech.mappie.validation.problems.enums

import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.util.kotlinFqName
import tech.mappie.resolving.EnumMappingRequest
import tech.mappie.resolving.enums.EnumMappingTarget
import tech.mappie.resolving.enums.ExplicitEnumMappingTarget
import tech.mappie.resolving.enums.ThrowingEnumMappingTarget
import tech.mappie.validation.Problem
import tech.mappie.validation.ValidationContext

class AllSourcesMappedProblems(
    private val mappings: Map<IrEnumEntry, List<EnumMappingTarget>>,
) {

    fun all(): List<Problem> = mappings.map { (source, targets) ->
        val name = "${source.parent.kotlinFqName.shortName().asString()}.${source.name.asString()}"
        when {
            targets.isEmpty() -> Problem.error("Source $name has no target defined")
            else -> Problem.error("Source $name has multiple targets defined")
        }
    }

    companion object {
        fun of(context: ValidationContext, mapping: EnumMappingRequest): AllSourcesMappedProblems {
            return if (context.configuration.strictness.enums) {
                AllSourcesMappedProblems(
                    mapping.mappings.filter { (_, targets) ->
                        targets.isEmpty() || targets.count { it is ExplicitEnumMappingTarget || it is ThrowingEnumMappingTarget } > 1
                    }
                )
            } else {
                AllSourcesMappedProblems(emptyMap())
            }
        }
    }
}