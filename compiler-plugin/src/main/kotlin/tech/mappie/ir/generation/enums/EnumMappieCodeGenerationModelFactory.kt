package tech.mappie.ir.generation.enums

import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import tech.mappie.ir.generation.EnumMappieCodeGenerationModel
import tech.mappie.ir.resolving.EnumMappingRequest
import tech.mappie.ir.MappieDefinition
import tech.mappie.ir.resolving.SourcesTargetEnumMappings
import tech.mappie.ir.resolving.SuperCallEnumMappings
import tech.mappie.ir.resolving.enums.EnumMappingTarget
import tech.mappie.ir.resolving.enums.ResolvedEnumMappingTarget

class EnumMappieCodeGenerationModelFactory() {

    @Suppress("UNCHECKED_CAST")
    fun construct(request: EnumMappingRequest, definition: MappieDefinition): EnumMappieCodeGenerationModel =
        EnumMappieCodeGenerationModel(
            definition,
            request.source,
            request.target,
            when (request.mappings) {
                is SuperCallEnumMappings -> request.mappings
                is SourcesTargetEnumMappings -> {
                    request.mappings
                        .mapValues { (_, targets) -> select(targets) }
                        .filterValues { it != null }
                        .let {
                            SourcesTargetEnumMappings(it as Map<IrEnumEntry, List<EnumMappingTarget>>)
                        }
                }
            }
        )

    private fun select(targets: List<EnumMappingTarget>): List<EnumMappingTarget>? =
        targets.firstOrNull { it !is ResolvedEnumMappingTarget }?.let { listOf(it) }
            ?: targets.firstOrNull { it is ResolvedEnumMappingTarget }?.let { listOf(it) }
}