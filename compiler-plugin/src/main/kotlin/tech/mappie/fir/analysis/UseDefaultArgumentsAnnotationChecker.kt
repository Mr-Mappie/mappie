package tech.mappie.fir.analysis

import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirAnnotationCallChecker
import org.jetbrains.kotlin.fir.expressions.FirAnnotationCall
import org.jetbrains.kotlin.fir.symbols.impl.FirClassSymbol
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.resolvedType
import tech.mappie.fir.analysis.MappieErrors.ANNOTATION_USE_DEFAULT_ARGUMENTS_NOT_APPLICABLE
import tech.mappie.fir.util.isSubclassOfEnumMappie
import tech.mappie.util.CLASS_ID_USE_DEFAULT_ARGUMENTS

class UseDefaultArgumentsAnnotationChecker : FirAnnotationCallChecker(MppCheckerKind.Common) {

    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(expression: FirAnnotationCall) {
        if (expression.resolvedType.classId == CLASS_ID_USE_DEFAULT_ARGUMENTS) {
            val symbol = expression.containingDeclarationSymbol
            if (symbol is FirClassSymbol && symbol.isSubclassOfEnumMappie()) {
                reporter.reportOn(expression.source, ANNOTATION_USE_DEFAULT_ARGUMENTS_NOT_APPLICABLE)
            }
        }
    }
}