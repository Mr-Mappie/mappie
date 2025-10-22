package tech.mappie.fir.resolving

import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirClassChecker
import org.jetbrains.kotlin.fir.declarations.FirClass
import tech.mappie.fir.util.isSubclassOfAnMappie
import tech.mappie.state.GlobalMappieState
import tech.mappie.state.MappieReference

class MappieCollector : FirClassChecker(MppCheckerKind.Common) {

    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirClass) {
        if (declaration.symbol.isSubclassOfAnMappie()) {
            GlobalMappieState.definitions.add(MappieReference.of(declaration))
        }
    }
}