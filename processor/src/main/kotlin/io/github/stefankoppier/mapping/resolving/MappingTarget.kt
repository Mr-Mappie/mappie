package io.github.stefankoppier.mapping.resolving

import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.IrType

sealed interface MappingTarget

data class ConstructorMappingTarget(
    val constructor: IrConstructor,
    val values: List<IrValueParameter>
) : MappingTarget

data class SingleResultMappingTarget(
    val type: IrType,
    val value: IrExpression,
) : MappingTarget