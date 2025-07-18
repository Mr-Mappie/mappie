package tech.mappie.fir.resolving.enums

import org.jetbrains.kotlin.fir.declarations.FirEnumEntry
import org.jetbrains.kotlin.fir.expressions.FirExpression

sealed interface EnumMappingTarget

data class ResolvedEnumMappingTarget(val entry: FirEnumEntry) : EnumMappingTarget

data class ThrownByEnumMappingTarget(val expression: FirExpression) : EnumMappingTarget