package tech.mappie.fir.resolving.classes

import org.jetbrains.kotlin.fir.symbols.impl.FirValueParameterSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType

sealed interface MappingTarget {
    val type: ConeKotlinType
}

data class ValueParameterTarget(val parameter: FirValueParameterSymbol) : MappingTarget {
    override val type = parameter.resolvedReturnType
}
