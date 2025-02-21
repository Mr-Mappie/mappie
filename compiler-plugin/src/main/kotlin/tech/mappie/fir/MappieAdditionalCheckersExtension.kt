package tech.mappie.fir

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.DeclarationCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.expression.ExpressionCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension
import tech.mappie.fir.analysis.*

class MappieAdditionalCheckersExtension(session: FirSession) : FirAdditionalCheckersExtension(session) {

    override val declarationCheckers = object : DeclarationCheckers() {
        override val classCheckers = setOf(
            ObjectMappieOverrideTypeChecker(),
        )
    }

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
}