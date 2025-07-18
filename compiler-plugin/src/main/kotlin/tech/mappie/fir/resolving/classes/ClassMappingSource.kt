package tech.mappie.fir.resolving.classes

import org.jetbrains.kotlin.fir.expressions.FirCallableReferenceAccess
import org.jetbrains.kotlin.fir.symbols.impl.FirPropertySymbol
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.resolvedType
import org.jetbrains.kotlin.fir.types.type
import tech.mappie.util.CLASS_ID_KPROPERTY0
import tech.mappie.util.CLASS_ID_KPROPERTY1

sealed interface ClassMappingSource {
    val type: ConeKotlinType
}

data class ExplicitPropertySource(val reference: FirCallableReferenceAccess) : ClassMappingSource {
    override val type: ConeKotlinType = run {
        val resolvedType = reference.resolvedType
        when (resolvedType.classId) {
            CLASS_ID_KPROPERTY1 -> resolvedType.typeArguments[1].type!!
            CLASS_ID_KPROPERTY0 -> resolvedType.typeArguments[0].type!!
            else -> error("ExplicitPropertySource.type for ${resolvedType.classId?.shortClassName}")
        }
    }
}

data class ImplicitPropertySource(val property: FirPropertySymbol) : ClassMappingSource {
    override val type: ConeKotlinType = property.resolvedReturnType
}
