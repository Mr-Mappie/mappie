package tech.mappie.fir.analysis

import org.jetbrains.kotlin.DeprecatedForRemovalCompilerApi
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
import tech.mappie.fir.util.isSubclassOfObjectMappie
import tech.mappie.util.CLASS_ID_USE_STRICT_ENUMS

class UseStrictEnumsAnnotationChecker : FirAnnotationCallChecker(MppCheckerKind.Common) {
    @OptIn(DeprecatedForRemovalCompilerApi::class)
    override fun check(expression: FirAnnotationCall, context: CheckerContext, reporter: DiagnosticReporter) {
        if (expression.resolvedType.classId == CLASS_ID_USE_STRICT_ENUMS) {
            val symbol = expression.containingDeclarationSymbol
            if (symbol is FirClassSymbol && symbol.isSubclassOfObjectMappie(context.session)) {
                reporter.reportOn(expression.source, ANNOTATION_NOT_APPLICABLE, NOT_APPLICABLE_MESSAGE, context)
            }
        }
    }

    companion object {
        private val ANNOTATION_NOT_APPLICABLE by warning1<KtElement, String>(WHOLE_ELEMENT)
        private const val NOT_APPLICABLE_MESSAGE = "Annotation @UseStrictEnums has no effect on children of ObjectMappie"
    }
}