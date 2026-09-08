package tech.mappie.ir.analysis.problems.classes

import org.jetbrains.kotlin.ir.util.constructedClass
import tech.mappie.ir.MappieContext
import tech.mappie.config.options.useStrictVisibility
import tech.mappie.ir.analysis.MappieIrAnalysisProblems.MAPPIE_NO_VISIBLE_CONSTRUCTOR
import tech.mappie.ir.resolving.ClassMappingRequest
import tech.mappie.ir.analysis.Problem

class VisibilityProblems(private val mapping: ClassMappingRequest) {

    private val constructor = mapping.constructor.symbol.owner

    context (context: MappieContext)
    fun all(): List<Problem> =
        if (!constructor.visibility.isPublicAPI && useStrictVisibility(mapping.origin.referenceMapFunction())) {
            listOf(
                Problem.Problem1(
                    MAPPIE_NO_VISIBLE_CONSTRUCTOR,
                    mapping.origin.referenceMapFunction(),
                    constructor.constructedClass.symbol,
                )
            )
        } else {
            emptyList()
        }

    companion object {
        fun of(mapping: ClassMappingRequest): VisibilityProblems =
            VisibilityProblems(mapping)
    }
}