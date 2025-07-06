package tech.mappie.fir.resolving.classes

import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType

sealed interface MappingSource {
    val type: ConeKotlinType
}

data class PropertySource(val property: FirPropertySymbol) : MappingSource {
    override val type: ConeKotlinType = property.resolvedReturnType
}
