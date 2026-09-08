package tech.mappie.ir.analysis.problems.classes

import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.isEnumClass
import tech.mappie.ir.analysis.MappieIrAnalysisProblems.MAPPIE_INCORRECT_TARGET_TYPE
import tech.mappie.ir.analysis.Problem
import tech.mappie.ir.resolving.ClassMappingRequest

class EnumConstructionProblems(
    private val mapping: ClassMappingRequest,
) {

    fun all(): List<Problem> =
        if (mapping.target.type.classOrNull?.owner?.isEnumClass == true) {
            listOf(
                Problem.Problem1(
                    MAPPIE_INCORRECT_TARGET_TYPE,
                    mapping.origin.referenceMapFunction(),
                    mapping.target.type
                )
            )
        } else {
            listOf()
        }

    companion object {
        fun of(mapping: ClassMappingRequest): EnumConstructionProblems {
            return EnumConstructionProblems(mapping)
        }
    }
}