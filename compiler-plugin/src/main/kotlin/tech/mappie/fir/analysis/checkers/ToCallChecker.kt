package tech.mappie.fir.analysis.checkers

import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies
import org.jetbrains.kotlin.diagnostics.error1
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.declarations.FirConstructor
import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.declarations.processAllDeclarations
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.arguments
import org.jetbrains.kotlin.fir.resolve.toRegularClassSymbol
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.types.resolvedType
import org.jetbrains.kotlin.fir.types.type
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.psi.KtElement
import tech.mappie.fir.util.toConstant
import tech.mappie.fir.util.hasCallableId
import tech.mappie.util.CLASS_ID_OBJECT_MAPPING_CONSTRUCTOR
import tech.mappie.util.IDENTIFIER_TO

class ToCallChecker : FirFunctionCallChecker(MppCheckerKind.Common) {

    @OptIn(SymbolInternals::class)
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(expression: FirFunctionCall) {
        if (expression.hasCallableId(CallableId(CLASS_ID_OBJECT_MAPPING_CONSTRUCTOR, IDENTIFIER_TO))) {
            val name = expression.arguments.first().toConstant(context.session)?.value as? String?

            if (name == null) {
                reporter.reportOn(
                    expression.source,
                    NON_CONSTANT_ERROR,
                    "Argument must be a compile-time constant",
                )
            } else {
                val target = expression.getTargetRegularClassSymbol()
                if (target != null) {
                    val targets = buildList {
                        target.fir.processAllDeclarations(context.session) { declaration ->
                            when (declaration.fir) {
                                is FirProperty -> add((declaration.fir as FirProperty).name)
                                is FirConstructor -> addAll((declaration.fir as FirConstructor).valueParameters.map { it.name })
                                else -> Unit
                            }
                        }
                    }

                    if (targets.none { it.asString().removePrefix("_") == name }) {
                        reporter.reportOn(
                            expression.source,
                            UNKNOWN_NAME_ERROR,
                            "Identifier $name does not occur as as setter or as a parameter in constructor",
                        )
                    }
                }
            }
        }
    }

    context (context: CheckerContext)
    private fun FirFunctionCall.getTargetRegularClassSymbol() =
        dispatchReceiver?.resolvedType?.typeArguments?.last()?.type?.toRegularClassSymbol(context.session)

    companion object {
        private val NON_CONSTANT_ERROR by error1<KtElement, String>(SourceElementPositioningStrategies.WHOLE_ELEMENT)
        private val UNKNOWN_NAME_ERROR by error1<KtElement, String>(SourceElementPositioningStrategies.WHOLE_ELEMENT)
    }
}