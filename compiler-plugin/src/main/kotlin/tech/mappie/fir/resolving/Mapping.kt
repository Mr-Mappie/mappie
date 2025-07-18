package tech.mappie.fir.resolving

import org.jetbrains.kotlin.fir.declarations.FirEnumEntry
import org.jetbrains.kotlin.fir.symbols.impl.FirConstructorSymbol
import tech.mappie.fir.resolving.classes.ClassMappingSource
import tech.mappie.fir.resolving.classes.ClassMappingTarget
import tech.mappie.fir.resolving.enums.EnumMappingTarget

sealed interface Mapping

data class ClassMapping(
    val constructor: FirConstructorSymbol,
    val mappings: Map<ClassMappingTarget, ClassMappingSource?>,
) : Mapping

data class EnumMapping(val mappings: Map<EnumMappingTarget?, FirEnumEntry>) : Mapping