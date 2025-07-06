package tech.mappie.fir.util

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.isList
import org.jetbrains.kotlin.fir.types.isSet
import org.jetbrains.kotlin.fir.types.isSubtypeOf
import org.jetbrains.kotlin.fir.types.type

// TODO: account for mutableList/Set
context (session: FirSession)
fun ConeKotlinType.isMappableFrom(other: ConeKotlinType): Boolean = when {
    (isList && other.isList) || (isSet && other.isSet) ->
        this.typeArguments.first().type!!.isMappableFrom(other.typeArguments.first().type!!)
    (isList xor other.isList) || (isSet xor other.isSet) ->
        false
    else ->
        isSubtypeOf(other, session)
}
