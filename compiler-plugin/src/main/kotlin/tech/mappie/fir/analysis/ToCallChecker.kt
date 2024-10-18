package tech.mappie.fir.analysis

import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies
import org.jetbrains.kotlin.diagnostics.error1
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirExpressionChecker
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.arguments
import org.jetbrains.kotlin.fir.references.toResolvedSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtElement
import tech.mappie.util.IDENTIFIER_TO
import tech.mappie.util.MAPPIE_PACKAGE
import tech.mappie.fir.util.isConstantLike

class ToCallChecker : FirExpressionChecker<FirFunctionCall>(MppCheckerKind.Common) {

    override fun check(expression: FirFunctionCall, context: CheckerContext, reporter: DiagnosticReporter) {
        if (expression.calleeReference.toResolvedSymbol<FirNamedFunctionSymbol>()?.callableId == TO_CALLABLE_ID) {
            if (!expression.arguments.first().isConstantLike(context)) {
                reporter.reportOn(expression.source, NON_CONSTANT_ERROR, "Argument must be a compile-time constant.", context)
            }
        }
    }

    companion object {
        private val NON_CONSTANT_ERROR by error1<KtElement, String>(SourceElementPositioningStrategies.WHOLE_ELEMENT)
        private val TO_CALLABLE_ID = CallableId(MAPPIE_PACKAGE, FqName("ObjectMappingConstructor"), IDENTIFIER_TO)
    }
}