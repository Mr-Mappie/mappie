package tech.mappie.fir.analysis

import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirFunctionChecker
import org.jetbrains.kotlin.fir.declarations.*
import org.jetbrains.kotlin.fir.expressions.*
import org.jetbrains.kotlin.fir.resolve.getContainingClass
import org.jetbrains.kotlin.fir.visitors.FirVisitor
import tech.mappie.fir.analysis.MappieErrors.MULTIPLE_MAPPING_CALLS
import tech.mappie.fir.util.isSubclassOfAnMappie
import tech.mappie.util.IDENTIFIER_MAPPING

class FunctionBodyChecker : FirFunctionChecker(MppCheckerKind.Common) {

    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirFunction) {
        if (declaration.symbol.hasBody && declaration.getContainingClass()?.symbol?.isSubclassOfAnMappie() ?: false) {
            val calls = mutableListOf<FirFunctionCall>()
            declaration.body?.accept(MappingFinder, calls)
            if (calls.size > 1) {
                reporter.reportOn(calls.first().source, MULTIPLE_MAPPING_CALLS)
            }
        }
    }
}

private object MappingFinder : FirVisitor<Unit, MutableList<FirFunctionCall>>() {
    override fun visitElement(element: FirElement, data: MutableList<FirFunctionCall>) =
        element.acceptChildren(this, data)

    override fun visitFunctionCall(functionCall: FirFunctionCall, data: MutableList<FirFunctionCall>) {
        if (functionCall.calleeReference.name == IDENTIFIER_MAPPING) {
            data.add(functionCall)
        }
        functionCall.acceptChildren(this, data)
    }
}