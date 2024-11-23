package tech.mappie.fir.analysis

import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies
import org.jetbrains.kotlin.diagnostics.error1
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirExpressionChecker
import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.arguments
import org.jetbrains.kotlin.fir.references.toResolvedSymbol
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.types.resolvedType
import org.jetbrains.kotlin.fir.types.toRegularClassSymbol
import org.jetbrains.kotlin.fir.types.type
import org.jetbrains.kotlin.psi.KtElement
import tech.mappie.fir.util.toConstant
import tech.mappie.util.CLASS_ID_OBJECT_MAPPING_CONSTRUCTOR

class ToCallChecker : FirExpressionChecker<FirFunctionCall>(MppCheckerKind.Common) {

    @OptIn(SymbolInternals::class)
    override fun check(expression: FirFunctionCall, context: CheckerContext, reporter: DiagnosticReporter) {
        if (expression.calleeReference.toResolvedSymbol<FirNamedFunctionSymbol>()?.callableId == CLASS_ID_OBJECT_MAPPING_CONSTRUCTOR) {
            val name = expression.arguments.first().toConstant(context)?.value as? String
            if (name == null) {
                reporter.reportOn(expression.source, NON_CONSTANT_ERROR, "Argument must be a compile-time constant.", context)
            } else {
                val symbol = expression.getTargetRegularClassSymbol(context.session)!!
                // TODO: should target either set method or constructor argument instead of properties
                if (symbol.fir.properties.none { it.name.asString().removePrefix("_") == name }) {
                    reporter.reportOn(expression.source, UNKNOWN_NAME_ERROR, "Identifier $name does not occur as as setter or as a parameter in constructor", context)
                }
            }
        }
    }

    private fun FirFunctionCall.getTargetRegularClassSymbol(session: FirSession) =
        dispatchReceiver!!.resolvedType.typeArguments.last().type?.toRegularClassSymbol(session)

    private val FirRegularClass.properties
        get() = declarations.filterIsInstance<FirProperty>()

    companion object {
        private val NON_CONSTANT_ERROR by error1<KtElement, String>(SourceElementPositioningStrategies.WHOLE_ELEMENT)
        private val UNKNOWN_NAME_ERROR by error1<KtElement, String>(SourceElementPositioningStrategies.WHOLE_ELEMENT)
    }
}