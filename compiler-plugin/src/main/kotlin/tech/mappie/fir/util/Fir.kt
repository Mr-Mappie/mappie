package tech.mappie.fir.util

import org.jetbrains.kotlin.fir.FirEvaluatorResult
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.expressions.FirExpression
import org.jetbrains.kotlin.fir.expressions.FirExpressionEvaluator
import org.jetbrains.kotlin.fir.expressions.FirLiteralExpression
import org.jetbrains.kotlin.fir.expressions.FirPropertyAccessExpression
import org.jetbrains.kotlin.fir.references.toResolvedPropertySymbol
import org.jetbrains.kotlin.fir.symbols.SymbolInternals

@OptIn(SymbolInternals::class)
fun FirExpression.isConstantLike(context: CheckerContext) = when (this) {
    is FirLiteralExpression -> true
    is FirPropertyAccessExpression -> calleeReference.toResolvedPropertySymbol()?.fir?.let { property ->
        when (val result = FirExpressionEvaluator.evaluatePropertyInitializer(property, context.session)) {
            is FirEvaluatorResult.Evaluated -> result.result is FirLiteralExpression
            else -> false
        }
    } ?: false
    else -> false
}