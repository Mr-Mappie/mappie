package tech.mappie.analysis.expressions

import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.error1
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirFunctionChecker
import org.jetbrains.kotlin.fir.declarations.FirFunction
import org.jetbrains.kotlin.fir.declarations.utils.isOverride
import org.jetbrains.kotlin.fir.declarations.utils.nameOrSpecialName
import org.jetbrains.kotlin.fir.expressions.FirAnonymousFunctionExpression
import org.jetbrains.kotlin.fir.expressions.FirBlock
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.FirReturnExpression
import org.jetbrains.kotlin.fir.expressions.arguments
import org.jetbrains.kotlin.fir.expressions.impl.FirFunctionCallImpl
import org.jetbrains.kotlin.fir.expressions.impl.FirSingleExpressionBlock
import org.jetbrains.kotlin.fir.expressions.toResolvedCallableSymbol
import org.jetbrains.kotlin.fir.visitors.FirVisitorVoid
import org.jetbrains.kotlin.psi.KtElement
import tech.mappie.util.*

class MapFunctionElementChecker : FirFunctionChecker(MppCheckerKind.Common) {
    override fun check(declaration: FirFunction, context: CheckerContext, reporter: DiagnosticReporter) {
        if (declaration.nameOrSpecialName == IDENTIFIER_MAP && declaration.isOverride && declaration.body != null) {
            when (val body = declaration.body) {
                is FirSingleExpressionBlock -> {
                    val statement = body.statement
                    if (statement is FirReturnExpression) {
                        val result = statement.result
                        if (result is FirFunctionCallImpl && result.calleeReference.name == IDENTIFIER_MAPPING) {
                            (result.arguments.first() as FirAnonymousFunctionExpression).anonymousFunction.body?.accept(Visitor(context, reporter))
                        }
                    }
                }
            }
        }
    }

    private class Visitor(private val context: CheckerContext, private val reporter: DiagnosticReporter) : FirVisitorVoid() {
        override fun visitElement(element: FirElement) {
            reportOn(element)
        }

        override fun visitBlock(block: FirBlock) {
            block.statements.forEach { it.accept(this) }
        }

        override fun visitFunctionCall(functionCall: FirFunctionCall) {
            if (functionCall.toResolvedCallableSymbol()?.name !in ALLOWED_METHODS) {
                reportOn(functionCall)
            }
        }

        override fun visitReturnExpression(returnExpression: FirReturnExpression) { }

        private fun reportOn(element: FirElement) =
            reporter.reportOn(element.source, ERROR, "Unexpected element ${element.source?.getElementTextInContextForDebug()}", context)

        companion object {
            private val ALLOWED_METHODS = listOf(
                IDENTIFIER_FROM_ENUM_ENTRY,
                IDENTIFIER_THROWN_BY_ENUM_ENTRY,
                IDENTIFIER_FROM_PROPERTY,
                IDENTIFIER_FROM_PROPERTY_NOT_NULL,
                IDENTIFIER_FROM_VALUE,
                IDENTIFIER_FROM_EXPRESSION,
                IDENTIFIER_VIA,
                IDENTIFIER_TRANSFORM,
            )
            private val ERROR by error1<KtElement, String>()
        }
    }
}