package tech.mappie.fir.analysis.checkers

import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies.WHOLE_ELEMENT
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.diagnostics.warning1
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirAnnotationCallChecker
import org.jetbrains.kotlin.fir.expressions.FirAnnotationCall
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.resolvedType
import org.jetbrains.kotlin.psi.KtElement
import tech.mappie.fir.util.isSubclassOfEnumMappie
import tech.mappie.util.CLASS_ID_USE_DEFAULT_ARGUMENTS

class UseDefaultArgumentsAnnotationChecker : FirAnnotationCallChecker(MppCheckerKind.Common) {

    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(expression: FirAnnotationCall) {
        if (expression.resolvedType.classId == CLASS_ID_USE_DEFAULT_ARGUMENTS) {
            val symbol = expression.containingDeclarationSymbol
            context(context.session) {
                if (symbol is FirClassSymbol && symbol.isSubclassOfEnumMappie()) {
                    reporter.reportOn(expression.source, ANNOTATION_NOT_APPLICABLE, NOT_APPLICABLE_MESSAGE)
                }
            }
        }
    }

    companion object {
        private val ANNOTATION_NOT_APPLICABLE by warning1<KtElement, String>(WHOLE_ELEMENT)
        private const val NOT_APPLICABLE_MESSAGE = "Annotation @UseDefaultArguments has no effect on subclass of EnumMappie"
    }
}