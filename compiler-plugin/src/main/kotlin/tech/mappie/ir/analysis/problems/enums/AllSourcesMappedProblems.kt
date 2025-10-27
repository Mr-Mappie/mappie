package tech.mappie.ir.analysis.problems.enums

import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.util.kotlinFqName
import tech.mappie.ir.MappieContext
import tech.mappie.config.options.useStrictEnums
import tech.mappie.ir.resolving.EnumMappingRequest
import tech.mappie.ir.resolving.enums.EnumMappingTarget
import tech.mappie.ir.resolving.enums.ExplicitEnumMappingTarget
import tech.mappie.ir.resolving.enums.ThrowingEnumMappingTarget
import tech.mappie.ir.analysis.Problem
import tech.mappie.ir.util.location

class AllSourcesMappedProblems(
    private val mapping: EnumMappingRequest,
    private val mappings: Map<IrEnumEntry, List<EnumMappingTarget>>,
) {

    fun all(): List<Problem> = mappings.map { (source, targets) ->
        val name = "${source.parent.kotlinFqName.shortName().asString()}.${source.name.asString()}"
        when {
            targets.isEmpty() -> Problem.error("Source $name has no target defined", location(mapping.origin))
            else -> Problem.error("Source $name has multiple targets defined", location(mapping.origin))
        }
    }

    companion object {

        context(context: MappieContext)
        fun of(mapping: EnumMappingRequest): AllSourcesMappedProblems {
            return if (context.useStrictEnums(mapping.origin)) {
                AllSourcesMappedProblems(
                    mapping,
                    mapping.mappings.filter { (_, targets) ->
                        targets.isEmpty() || targets.count { it is ExplicitEnumMappingTarget || it is ThrowingEnumMappingTarget } > 1
                    }
                )
            } else {
                AllSourcesMappedProblems(mapping, emptyMap())
            }
        }
    }
}