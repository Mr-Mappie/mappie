package tech.mappie.fir.analysis

import org.jetbrains.kotlin.diagnostics.DiagnosticReporter
import org.jetbrains.kotlin.diagnostics.reportOn
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.declarations.FirConstructor
import org.jetbrains.kotlin.fir.declarations.FirProperty
import org.jetbrains.kotlin.fir.declarations.processAllDeclarations
import org.jetbrains.kotlin.fir.expressions.FirFunctionCall
import org.jetbrains.kotlin.fir.expressions.arguments
import org.jetbrains.kotlin.fir.java.declarations.FirJavaMethod
import org.jetbrains.kotlin.fir.resolve.toRegularClassSymbol
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.types.resolvedType
import org.jetbrains.kotlin.fir.types.type
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name
import tech.mappie.fir.analysis.MappieErrors.NON_CONSTANT_ERROR
import tech.mappie.fir.analysis.MappieErrors.UNKNOWN_NAME_ERROR
import tech.mappie.fir.util.toConstant
import tech.mappie.fir.util.hasCallableId
import tech.mappie.fir.util.isJavaSetter
import tech.mappie.util.CLASS_ID_MULTIPLE_OBJECT_MAPPING_CONSTRUCTOR
import tech.mappie.util.CLASS_ID_OBJECT_MAPPING_CONSTRUCTOR
import tech.mappie.util.IDENTIFIER_TO

class ToCallChecker : FirFunctionCallChecker(MppCheckerKind.Common) {

    @OptIn(SymbolInternals::class)
    context(context: CheckerContext, reporter: DiagnosticReporter)
    override fun check(expression: FirFunctionCall) {
        if (expression.isToCall()) {
            val name = expression.arguments.first().toConstant()?.value as? String?

            if (name == null) {
                reporter.reportOn(expression.source, NON_CONSTANT_ERROR)
            } else {
                val target = expression.getTargetRegularClassSymbol()
                if (target != null) {
                    val targets = buildSet {
                        target.fir.processAllDeclarations(context.session) { declaration ->
                            when (declaration.fir) {
                                is FirProperty -> add((declaration.fir as FirProperty).name)
                                is FirConstructor -> addAll((declaration.fir as FirConstructor).valueParameters.map { it.name })
                                is FirJavaMethod -> {
                                    val method = declaration.fir as FirJavaMethod
                                    if (method.isJavaSetter()) {
                                        val name = method.name.asString().removePrefix("set").replaceFirstChar { it.lowercaseChar() }
                                        add(Name.identifier(name))
                                    }
                                }
                                else -> Unit
                            }
                        }
                    }

                    if (targets.none { it.asString().removePrefix("_") == name }) {
                        reporter.reportOn(expression.source, UNKNOWN_NAME_ERROR, name)
                    }
                }
            }
        }
    }

    private fun FirFunctionCall.isToCall() =
        hasCallableId(CallableId(CLASS_ID_OBJECT_MAPPING_CONSTRUCTOR, IDENTIFIER_TO))
            || hasCallableId(CallableId(CLASS_ID_MULTIPLE_OBJECT_MAPPING_CONSTRUCTOR, IDENTIFIER_TO))

    context (context: CheckerContext)
    private fun FirFunctionCall.getTargetRegularClassSymbol() =
        dispatchReceiver?.resolvedType?.typeArguments?.last()?.type?.toRegularClassSymbol()
}