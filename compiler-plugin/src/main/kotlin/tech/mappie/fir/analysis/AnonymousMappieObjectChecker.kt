package tech.mappie.fir.analysis

import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies.WHOLE_ELEMENT
import org.jetbrains.kotlin.diagnostics.error1
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirAnonymousObjectChecker
import org.jetbrains.kotlin.fir.declarations.FirAnonymousObject
import org.jetbrains.kotlin.psi.KtElement
import tech.mappie.fir.util.isSubclassOfAnMappie

class AnonymousMappieObjectChecker : FirAnonymousObjectChecker(MppCheckerKind.Common) {

    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(declaration: FirAnonymousObject) {
        if (declaration.symbol.isSubclassOfAnMappie()) {
            reporter.reportOn(declaration.source, INVALID_ANONYMOUS_OBJECT, INVALID_ANONYMOUS_OBJECT_MESSAGE)
        }
    }

    companion object {
        private val INVALID_ANONYMOUS_OBJECT by error1<KtElement, String>(WHOLE_ELEMENT)
        private const val INVALID_ANONYMOUS_OBJECT_MESSAGE = "Anonymous Mappie objects are not supported"
    }
}