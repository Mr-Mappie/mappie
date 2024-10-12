package tech.mappie

import org.jetbrains.kotlin.diagnostics.*
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.MppCheckerKind
import org.jetbrains.kotlin.fir.analysis.checkers.context.CheckerContext
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.DeclarationCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirClassChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.ExpressionCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirCallChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirExpressionChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension
import org.jetbrains.kotlin.fir.declarations.FirClass
import org.jetbrains.kotlin.fir.declarations.FirEnumEntry
import org.jetbrains.kotlin.fir.declarations.utils.isEnumClass
import org.jetbrains.kotlin.fir.expressions.*
import org.jetbrains.kotlin.fir.expressions.impl.FirFunctionCallImpl
import org.jetbrains.kotlin.fir.extensions.FirExtensionRegistrar
import org.jetbrains.kotlin.fir.references.symbol
import org.jetbrains.kotlin.fir.symbols.impl.FirEnumEntrySymbol
import org.jetbrains.kotlin.fir.types.*
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.psi.KtElement
import tech.mappie.util.IDENTIFIER_TO

class MappieFirRegistrar : FirExtensionRegistrar() {

    override fun ExtensionRegistrarContext.configurePlugin() {
        +::MappieCheckers
    }
}

class MappieCheckers(session: FirSession) : FirAdditionalCheckersExtension(session) {
    override val declarationCheckers: DeclarationCheckers = object : DeclarationCheckers() {
        override val classCheckers: Set<FirClassChecker> = setOf(MappieDeclarationChecker())
    }

    override val expressionCheckers: ExpressionCheckers = object : ExpressionCheckers() {
        override val functionCallCheckers: Set<FirFunctionCallChecker> = setOf(ToCallChecker())
    }
}

class ToCallChecker : FirExpressionChecker<FirFunctionCall>(MppCheckerKind.Common) {

    @OptIn(UnresolvedExpressionTypeAccess::class)
    override fun check(expression: FirFunctionCall, context: CheckerContext, reporter: DiagnosticReporter) {
        if (expression.calleeReference.name == IDENTIFIER_TO) {
            val argument = expression.arguments.first()
            if (argument is FirLiteralExpression) {
//                val x = expression.dispatchReceiver!!.coneTypeOrNull!!.typeArguments.first()
            } else {
                reporter.reportOn(expression.source, NON_CONSTANT_ERROR, "Argument must be a compile-time constant", context)
            }
        }
    }

    companion object {
        val NON_CONSTANT_ERROR by error1<KtElement, String>(SourceElementPositioningStrategies.WHOLE_ELEMENT)
    }
}

class MappieDeclarationChecker : FirClassChecker(MppCheckerKind.Common) {
    override fun check(declaration: FirClass, context: CheckerContext, reporter: DiagnosticReporter) {
        val mappie = declaration.superTypeRefs.firstOrNull { it.coneType.classId == ClassId(FqName("tech.mappie.api"), Name.identifier("ObjectMappie")) }
        if (mappie != null) {
            val source = mappie.coneType.typeArguments[0].type!!.toClassSymbol(context.session)!!
            val target = mappie.coneType.typeArguments[1].type!!.toClassSymbol(context.session)!!

            if (source.isEnumClass && target.isEnumClass) {
                source.declarationSymbols.filterIsInstance<FirEnumEntrySymbol>()
            }
        }
    }
}
