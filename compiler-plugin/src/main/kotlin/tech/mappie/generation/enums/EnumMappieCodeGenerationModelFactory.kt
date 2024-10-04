package tech.mappie.generation.enums

import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.declarations.IrFunction
import tech.mappie.generation.CodeGenerationModelFactory
import tech.mappie.generation.EnumMappieCodeGenerationModel
import tech.mappie.resolving.EnumMappingRequest
import tech.mappie.resolving.enums.EnumMappingTarget
import tech.mappie.resolving.enums.ResolvedEnumMappingTarget

class EnumMappieCodeGenerationModelFactory(private val request: EnumMappingRequest) : CodeGenerationModelFactory {

    @Suppress("UNCHECKED_CAST")
    override fun construct(function: IrFunction): EnumMappieCodeGenerationModel =
        EnumMappieCodeGenerationModel(
            function,
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