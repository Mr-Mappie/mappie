package io.github.stefankoppier.mapping.resolving

import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrValueParameter

sealed interface MappingTarget

data class ConstructorMappingTarget(
    val constructor: IrConstructor,
    val values: List<IrValueParameter>
) : MappingTarget

