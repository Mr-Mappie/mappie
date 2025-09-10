package tech.mappie.fir.analysis

import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirAnonymousObjectChecker
import org.jetbrains.kotlin.fir.declarations.FirAnonymousObject
import tech.mappie.fir.analysis.MappieErrors.INVALID_ANONYMOUS_OBJECT
import tech.mappie.fir.util.isSubclassOfAnMappie

class AnonymousMappieObjectChecker : FirAnonymousObjectChecker(MppCheckerKind.Common) {

    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirAnonymousObject) {
        if (declaration.symbol.isSubclassOfAnMappie()) {
            reporter.reportOn(declaration.source, INVALID_ANONYMOUS_OBJECT)
        }
    }
}