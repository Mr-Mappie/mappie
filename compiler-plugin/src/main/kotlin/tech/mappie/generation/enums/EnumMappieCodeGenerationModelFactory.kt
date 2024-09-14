package tech.mappie.generation.enums

import org.jetbrains.kotlin.ir.declarations.IrFunction
import tech.mappie.generation.EnumMappieCodeGenerationModel
import tech.mappie.resolving.EnumMappingRequest
import tech.mappie.resolving.enums.EnumMappingTarget
import tech.mappie.resolving.enums.ResolvedEnumMappingTarget

class EnumMappieCodeGenerationModelFactory {

    fun construct(function: IrFunction, request: EnumMappingRequest): EnumMappieCodeGenerationModel =
        EnumMappieCodeGenerationModel(
            function,
            request.source,
            request.target,
            request.mappings.mapValues { (_, targets) -> select(targets) }
        )

    private fun select(targets: List<EnumMappingTarget>): EnumMappingTarget =
        targets.firstOrNull { it !is ResolvedEnumMappingTarget }
            ?: targets.first { it is ResolvedEnumMappingTarget }
}