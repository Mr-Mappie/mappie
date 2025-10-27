package tech.mappie.ir.generation.enums

import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import tech.mappie.ir.generation.EnumMappieCodeGenerationModel
import tech.mappie.ir.resolving.EnumMappingRequest
import tech.mappie.ir.resolving.MappieDefinition
import tech.mappie.ir.resolving.enums.EnumMappingTarget
import tech.mappie.ir.resolving.enums.ResolvedEnumMappingTarget

class EnumMappieCodeGenerationModelFactory(private val request: EnumMappingRequest) {

    @Suppress("UNCHECKED_CAST")
    fun construct(definition: MappieDefinition): EnumMappieCodeGenerationModel =
        EnumMappieCodeGenerationModel(
            definition,
            request.source,
            request.target,
            request.mappings
                .mapValues { (_, targets) -> select(targets) }
                .filterValues { it != null } as Map<IrEnumEntry, EnumMappingTarget>
        )

    private fun select(targets: List<EnumMappingTarget>): EnumMappingTarget? =
        targets.firstOrNull { it !is ResolvedEnumMappingTarget }
            ?: targets.firstOrNull { it is ResolvedEnumMappingTarget }
}