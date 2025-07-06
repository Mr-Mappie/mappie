package tech.mappie.fir.analysis

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.expression.ExpressionCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension
import tech.mappie.fir.analysis.checkers.CompileTimeDslReceiverChecker
import tech.mappie.fir.analysis.checkers.ToCallChecker
import tech.mappie.fir.analysis.checkers.UnnecessaryExplicitEnumMappingChecker
import tech.mappie.fir.analysis.checkers.UseDefaultArgumentsAnnotationChecker
import tech.mappie.fir.analysis.checkers.UseStrictEnumsAnnotationChecker
import tech.mappie.fir.analysis.checkers.UseStrictVisibilityAnnotationChecker

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
}