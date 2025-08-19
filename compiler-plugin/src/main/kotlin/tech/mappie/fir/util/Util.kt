package tech.mappie.fir.util

import org.jetbrains.kotlin.fir.FirEvaluatorResult
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.expressions.*
import org.jetbrains.kotlin.fir.java.declarations.FirJavaMethod
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

context(context: CheckerContext)
internal fun FirClassSymbol<*>.isSubclassOfEnumMappie() =
    isSubclassOf(CLASS_ID_ENUM_MAPPIE.toLookupTag(), context.session, false, false)

context(context: CheckerContext)
internal fun FirClassSymbol<*>.isSubclassOfObjectMappie() =
    isSubclassOf(CLASS_ID_OBJECT_MAPPIE.toLookupTag(), context.session, false, false)

context(context: CheckerContext)
internal fun FirClassSymbol<*>.isSubclassOfObjectMappie2() =
    isSubclassOf(CLASS_ID_OBJECT_MAPPIE2.toLookupTag(), context.session, false, false)

context(context: CheckerContext)
internal fun FirClassSymbol<*>.isSubclassOfObjectMappie3() =
    isSubclassOf(CLASS_ID_OBJECT_MAPPIE3.toLookupTag(), context.session, false, false)

context(context: CheckerContext)
internal fun FirClassSymbol<*>.isSubclassOfObjectMappie4() =
    isSubclassOf(CLASS_ID_OBJECT_MAPPIE4.toLookupTag(), context.session, false, false)

context(context: CheckerContext)
internal fun FirClassSymbol<*>.isSubclassOfObjectMappie5() =
    isSubclassOf(CLASS_ID_OBJECT_MAPPIE4.toLookupTag(), context.session, false, false)

context(context: CheckerContext)
internal fun FirClassSymbol<*>.isSubclassOfAnObjectMappie() =
    isSubclassOfObjectMappie() || isSubclassOfObjectMappie2() || isSubclassOfObjectMappie3() || isSubclassOfObjectMappie4() || isSubclassOfObjectMappie5()

context(context: CheckerContext)
internal fun FirClassSymbol<*>.isSubclassOfAnMappie() =
    isSubclassOfAnObjectMappie() || isSubclassOfEnumMappie()

context(context: CheckerContext)
fun FirJavaMethod.isJavaGetter(): Boolean {
    return name.asString().startsWith("get") && valueParameters.isEmpty()
}

@OptIn(SymbolInternals::class)
context (context: CheckerContext)
fun FirExpression.toConstant(): FirLiteralExpression? =
    when (this) {
        is FirLiteralExpression -> this
        is FirPropertyAccessExpression -> calleeReference.toResolvedPropertySymbol()?.fir?.let { property ->
            when (val result = FirExpressionEvaluator.evaluatePropertyInitializer(property, context.session)) {
                is FirEvaluatorResult.Evaluated -> result.result as FirLiteralExpression
                else -> null
            }
        }
        else -> null
    }

fun FirFunctionCall.hasCallableId(callableId: CallableId) =
    (calleeReference.resolved?.resolvedSymbol as? FirFunctionSymbol)?.callableId == callableId
