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

internal fun FirClassSymbol<*>.isSubclassOfEnumMappie(session: FirSession) =
    isSubclassOf(CLASS_ID_ENUM_MAPPIE.toLookupTag(), session, false, false)

internal fun FirClassSymbol<*>.isSubclassOfObjectMappie(session: FirSession) =
    isSubclassOf(CLASS_ID_OBJECT_MAPPIE.toLookupTag(), session, false, false)

@OptIn(SymbolInternals::class)
fun FirExpression.toConstant(context: CheckerContext): FirLiteralExpression? =
    when (this) {
        is FirLiteralExpression -> this
        is FirPropertyAccessExpression -> calleeReference.toResolvedPropertySymbol()?.fir?.let { property ->
            when (val result = FirExpressionEvaluator.evaluatePropertyInitializer(property, context.session)) {
                is FirEvaluatorResult.Evaluated -> result.result as FirLiteralExpression // TODO: does this work?
                else -> null
            }
        }
        else -> null
    }

fun FirFunctionCall.hasCallableId(callableId: CallableId) =
    (calleeReference.resolved?.resolvedSymbol as? FirFunctionSymbol)?.callableId == callableId
