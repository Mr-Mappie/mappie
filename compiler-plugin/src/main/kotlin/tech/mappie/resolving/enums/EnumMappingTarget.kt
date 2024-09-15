package tech.mappie.resolving.enums

import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.expressions.*

sealed interface EnumMappingTarget

data class ResolvedEnumMappingTarget(val target: IrEnumEntry) : EnumMappingTarget

data class ExplicitEnumMappingTarget(val target: IrExpression) : EnumMappingTarget

data class ThrowingEnumMappingTarget(val exception: IrExpression) : EnumMappingTarget
