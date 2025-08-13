package tech.mappie.fir

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.DeclarationCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.FirAnonymousObjectChecker
import org.jetbrains.kotlin.fir.analysis.checkers.expression.ExpressionCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension
import tech.mappie.fir.analysis.*

class MappieAdditionalCheckersExtension(session: FirSession) : FirAdditionalCheckersExtension(session) {

    override val expressionCheckers = object : ExpressionCheckers() {
        override val annotationCallCheckers = setOf(
            UseDefaultArgumentsAnnotationChecker(),
            UseStrictVisibilityAnnotationChecker(),
            UseStrictEnumsAnnotationChecker(),
        )

        override val functionCallCheckers: Set<FirFunctionCallChecker> = setOf(
            UnnecessaryExplicitEnumMappingChecker(),
            CompileTimeDslReceiverChecker(),
            ToCallChecker(),
        )
    }

    override val declarationCheckers: DeclarationCheckers = object : DeclarationCheckers() {
        override val anonymousObjectCheckers: Set<FirAnonymousObjectChecker> = setOf(
            AnonymousMappieObjectChecker()
        )
    }
}