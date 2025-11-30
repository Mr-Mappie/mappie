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
import tech.mappie.ir.resolving.SourcesTargetEnumMappings
import tech.mappie.ir.resolving.SuperCallEnumMappings
import tech.mappie.ir.util.location

class AllSourcesMappedProblems(
    private val mapping: EnumMappingRequest,
    private val mappings: Map<IrEnumEntry, List<EnumMappingTarget>>,
) {

    fun all(): List<Problem> = mappings.map { (source, targets) ->
        val name = "${source.parent.kotlinFqName.shortName().asString()}.${source.name.asString()}"
        when {
            targets.isEmpty() -> Problem.error("Source $name has no target defined", location(mapping.origin.referenceMapFunction()))
            else -> Problem.error("Source $name has multiple targets defined", location(mapping.origin.referenceMapFunction()))
        }
    }

    companion object {

        context(context: MappieContext)
        fun of(mapping: EnumMappingRequest): AllSourcesMappedProblems {
            val mappings = mapping.mappings

            return if (useStrictEnums(mapping.origin.referenceMapFunction())) {
                return when (mappings) {
                    is SuperCallEnumMappings -> AllSourcesMappedProblems(mapping, emptyMap())
                    is SourcesTargetEnumMappings -> {
                        AllSourcesMappedProblems(
                            mapping,
                            mappings.filter { (_, targets) ->
                                targets.isEmpty() || targets.count { it is ExplicitEnumMappingTarget || it is ThrowingEnumMappingTarget } > 1
                            }
                        )
                    }
                }
            } else {
                AllSourcesMappedProblems(mapping, emptyMap())
            }
        }
    }
}