package tech.mappie.ir.analysis.problems.enums

import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.util.kotlinFqName
import tech.mappie.ir.MappieContext
import tech.mappie.config.options.useStrictEnums
import tech.mappie.ir.analysis.MappieIrAnalysisProblems.MAPPIE_MULTIPLE_MAPPING_TARGETS
import tech.mappie.ir.analysis.MappieIrAnalysisProblems.MAPPIE_NO_MAPPING_TARGET
import tech.mappie.ir.analysis.Problem
import tech.mappie.ir.resolving.EnumMappingRequest
import tech.mappie.ir.resolving.enums.EnumMappingTarget
import tech.mappie.ir.resolving.enums.ExplicitEnumMappingTarget
import tech.mappie.ir.resolving.enums.ThrowingEnumMappingTarget
import tech.mappie.ir.resolving.SourcesTargetEnumMappings
import tech.mappie.ir.resolving.SuperCallEnumMappings

class AllSourcesMappedProblems(
    private val mapping: EnumMappingRequest,
    private val mappings: Map<IrEnumEntry, List<EnumMappingTarget>>,
) {

    fun all(): List<Problem> = mappings.map { (source, targets) ->
        val name = "${source.parent.kotlinFqName.shortName().asString()}.${source.name.asString()}"
        return when {
            targets.isEmpty() -> listOf(
                Problem.Problem1(MAPPIE_NO_MAPPING_TARGET, mapping.origin.referenceMapFunction(), name)
            )
            else -> listOf(
                Problem.Problem1(MAPPIE_MULTIPLE_MAPPING_TARGETS, mapping.origin.referenceMapFunction(), name)
            )
        }
    }

    companion object {
        context(context: MappieContext)
        fun of(mapping: EnumMappingRequest): AllSourcesMappedProblems {
            val mappings = mapping.mappings

            return if (useStrictEnums(mapping.origin.referenceMapFunction())) {
                when (mappings) {
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