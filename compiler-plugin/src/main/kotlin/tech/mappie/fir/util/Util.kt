package tech.mappie.fir.util

import org.jetbrains.kotlin.fir.FirEvaluatorResult
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.expressions.*
import org.jetbrains.kotlin.fir.references.resolved
import org.jetbrains.kotlin.fir.references.toResolvedPropertySymbol
import org.jetbrains.kotlin.fir.resolve.isSubclassOf
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirFunctionSymbol
import org.jetbrains.kotlin.fir.types.toLookupTag
import org.jetbrains.kotlin.name.CallableId
import tech.mappie.util.CLASS_ID_ENUM_MAPPIE
import tech.mappie.util.CLASS_ID_OBJECT_MAPPIE
import tech.mappie.util.CLASS_ID_OBJECT_MAPPIE2
import tech.mappie.util.CLASS_ID_OBJECT_MAPPIE3
import tech.mappie.util.CLASS_ID_OBJECT_MAPPIE4

context(session: FirSession)
internal fun FirClassSymbol<*>.isSubclassOfEnumMappie() =
    isSubclassOf(CLASS_ID_ENUM_MAPPIE.toLookupTag(), session, false, false)

context(session: FirSession)
internal fun FirClassSymbol<*>.isSubclassOfAnObjectMappie() =
    isSubclassOf(CLASS_ID_OBJECT_MAPPIE.toLookupTag(), session, false, false) ||
    isSubclassOf(CLASS_ID_OBJECT_MAPPIE2.toLookupTag(), session, false, false) ||
    isSubclassOf(CLASS_ID_OBJECT_MAPPIE3.toLookupTag(), session, false, false) ||
    isSubclassOf(CLASS_ID_OBJECT_MAPPIE4.toLookupTag(), session, false, false)

@OptIn(SymbolInternals::class)
fun FirExpression.toConstant(session: FirSession): FirLiteralExpression? =
    when (this) {
        is FirLiteralExpression -> this
        is FirPropertyAccessExpression -> calleeReference.toResolvedPropertySymbol()?.fir?.let { property ->
            when (val result = FirExpressionEvaluator.evaluatePropertyInitializer(property, session)) {
                is FirEvaluatorResult.Evaluated -> result.result as FirLiteralExpression
                else -> null
            }
        }
        else -> null
    }

fun FirFunctionCall.hasCallableId(callableId: CallableId) =
    (calleeReference.resolved?.resolvedSymbol as? FirFunctionSymbol)?.callableId == callableId
