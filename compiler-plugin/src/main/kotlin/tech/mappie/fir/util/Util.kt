package tech.mappie.fir.util

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.resolve.isSubclassOf
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.types.toLookupTag
import tech.mappie.util.CLASS_ID_ENUM_MAPPIE
import tech.mappie.util.CLASS_ID_OBJECT_MAPPIE

internal fun FirClassSymbol<*>.isSubclassOfEnumMappie(session: FirSession) =
    isSubclassOf(CLASS_ID_ENUM_MAPPIE.toLookupTag(), session, false, false)

internal fun FirClassSymbol<*>.isSubclassOfObjectMappie(session: FirSession) =
    isSubclassOf(CLASS_ID_OBJECT_MAPPIE.toLookupTag(), session, false, false)
