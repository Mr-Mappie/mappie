package tech.mappie.fir.analysis

import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.resolvedType
import tech.mappie.fir.analysis.MappieErrors.COMPILE_TIME_EXTENSION_RECEIVER
import tech.mappie.fir.analysis.MappieErrors.COMPILE_TIME_RECEIVER
import tech.mappie.util.ALL_MAPPING_FUNCTIONS
import tech.mappie.util.CLASS_ID_MULTIPLE_OBJECT_MAPPING_CONSTRUCTOR
import tech.mappie.util.CLASS_ID_OBJECT_MAPPING_CONSTRUCTOR

class CompileTimeDslReceiverChecker : FirFunctionCallChecker(MppCheckerKind.Common) {

    private val targets = listOf(CLASS_ID_OBJECT_MAPPING_CONSTRUCTOR, CLASS_ID_MULTIPLE_OBJECT_MAPPING_CONSTRUCTOR)

    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(expression: FirFunctionCall) {
        val name = expression.calleeReference.name
        if (name in ALL_MAPPING_FUNCTIONS) {
            return
        }

        if (expression.dispatchReceiver?.resolvedType?.classId in targets) {
            reporter.reportOn(expression.source, COMPILE_TIME_RECEIVER, name)
        }

        if (expression.extensionReceiver?.resolvedType?.classId in targets) {
            reporter.reportOn(expression.source, COMPILE_TIME_EXTENSION_RECEIVER, name)
        }
    }
}