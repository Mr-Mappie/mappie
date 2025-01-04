package tech.mappie.fir

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.analysis.checkers.expression.ExpressionCheckers
import org.jetbrains.kotlin.fir.analysis.checkers.expression.FirFunctionCallChecker
import org.jetbrains.kotlin.fir.analysis.extensions.FirAdditionalCheckersExtension
import tech.mappie.fir.analysis.UnnecessaryExplicitEnumMappingChecker
import tech.mappie.fir.analysis.UseDefaultArgumentsAnnotationChecker
import tech.mappie.fir.analysis.UseStrictEnumsAnnotationChecker
import tech.mappie.fir.analysis.UseStrictVisibilityAnnotationChecker

class MappieAdditionalCheckersExtension(session: FirSession) : FirAdditionalCheckersExtension(session) {

    override val expressionCheckers = object : ExpressionCheckers() {
        override val annotationCallCheckers = setOf(
            UseDefaultArgumentsAnnotationChecker(),
            UseStrictVisibilityAnnotationChecker(),
            UseStrictEnumsAnnotationChecker(),
        )

        override val functionCallCheckers: Set<FirFunctionCallChecker> = setOf(
            UnnecessaryExplicitEnumMappingChecker(),
        )
    }
}