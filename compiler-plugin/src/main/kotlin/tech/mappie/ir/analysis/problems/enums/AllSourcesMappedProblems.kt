package tech.mappie.ir.analysis.problems.enums

import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.util.kotlinFqName
import tech.mappie.config.options.useStrictEnums
import tech.mappie.ir.resolving.EnumMappingRequest
import tech.mappie.ir.resolving.enums.EnumMappingTarget
import tech.mappie.ir.resolving.enums.ExplicitEnumMappingTarget
import tech.mappie.ir.resolving.enums.ThrowingEnumMappingTarget
import tech.mappie.ir.analysis.Problem
import tech.mappie.ir.analysis.ValidationContext

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
            return if (context.useStrictEnums(context.function)) {
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