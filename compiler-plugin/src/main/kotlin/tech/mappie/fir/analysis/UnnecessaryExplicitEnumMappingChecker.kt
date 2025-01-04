package tech.mappie.fir.analysis

import org.jetbrains.kotlin.diagnostics.*
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies.WHOLE_ELEMENT
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirPropertyAccessExpression
import org.jetbrains.kotlin.fir.expressions.arguments
import org.jetbrains.kotlin.fir.references.FirNamedReference
import org.jetbrains.kotlin.fir.references.FirResolvedNamedReference
import org.jetbrains.kotlin.fir.symbols.impl.FirEnumEntrySymbol
import org.jetbrains.kotlin.psi.KtElement
import tech.mappie.util.IDENTIFIER_FROM_ENUM_ENTRY

class UnnecessaryExplicitEnumMappingChecker : FirFunctionCallChecker(MppCheckerKind.Common) {

    override fun check(expression: FirFunctionCall, context: CheckerContext, reporter: DiagnosticReporter) {
        if (expression.calleeReference.name == IDENTIFIER_FROM_ENUM_ENTRY) {
            val lhs = expression.extensionReceiver
            val rhs = expression.arguments.first()

            if (lhs is FirPropertyAccessExpression && rhs is FirPropertyAccessExpression) {
                val lhsReference = (lhs.calleeReference as? FirNamedReference) ?: return
                val rhsReference = (rhs.calleeReference as? FirNamedReference) ?: return

                if (lhsReference.name == rhsReference.name) {
                    val name = "${className(rhsReference)?.let { "$it." }}${rhsReference.name}"

                    reporter.reportOn(
                        expression.source,
                        UNNECESSARY_EXPLICIT_MAPPING,
                        "Unnecessary explicit mapping of source $name",
                        context
                    )
                }
            }
        }
    }

    private fun className(reference: FirNamedReference) =
        (reference as? FirResolvedNamedReference)
            ?.let { it.resolvedSymbol as? FirEnumEntrySymbol }
            ?.let { it.callableId.className?.shortName() }

    companion object {
        private val UNNECESSARY_EXPLICIT_MAPPING by warning1<KtElement, String>(WHOLE_ELEMENT)
    }
}