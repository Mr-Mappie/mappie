package tech.mappie.fir.resolving.classes

import org.jetbrains.kotlin.fir.symbols.impl.FirValueParameterSymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.name.Name

sealed interface ClassMappingTarget {
    val type: ConeKotlinType
    val name: Name
}

data class ValueParameterTarget(val parameter: FirValueParameterSymbol) : ClassMappingTarget {
    override val type = parameter.resolvedReturnType
    override val name: Name = parameter.name
}

data class NamedValueParameterTarget(val parameter: Name, override val type: ConeKotlinType) : ClassMappingTarget {
    override val name: Name = parameter
}