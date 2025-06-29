package tech.mappie.fir.analysis

import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.SourceElementPositioningStrategies.WHOLE_ELEMENT
import org.jetbrains.kotlin.diagnostics.error1
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.fir.types.resolvedType
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtElement
import tech.mappie.util.ALL_MAPPING_FUNCTIONS
import tech.mappie.util.CLASS_ID_OBJECT_MAPPING_CONSTRUCTOR

class CompileTimeDslReceiverChecker : FirFunctionCallChecker(MppCheckerKind.Common) {

    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(expression: FirFunctionCall) {
        val name = expression.calleeReference.name
        if (name in ALL_MAPPING_FUNCTIONS) {
            return
        }

        if (expression.dispatchReceiver?.resolvedType?.classId == CLASS_ID_OBJECT_MAPPING_CONSTRUCTOR) {
            reporter.reportOn(
                expression.source,
                COMPILE_TIME_RECEIVER,
                buildString {
                    append("The function $name was called on the mapping dsl which does not exist after compilation")
                    specify(name)
                },
            )
        }

        if (expression.extensionReceiver?.resolvedType?.classId == CLASS_ID_OBJECT_MAPPING_CONSTRUCTOR) {
            reporter.reportOn(
                expression.source,
                COMPILE_TIME_RECEIVER,
                buildString {
                    append("The function $name was called as an extension method on the mapping dsl which does not exist after compilation")
                    specify(name)
                }
            )
        }
    }

    private fun StringBuilder.specify(name: Name) =
        if (name == Name.identifier("run")) {
            append(". Did you mean to use kotlin.run?")
        } else {
            this
        }

    companion object {
        private val COMPILE_TIME_RECEIVER by error1<KtElement, String>(WHOLE_ELEMENT)
    }
}