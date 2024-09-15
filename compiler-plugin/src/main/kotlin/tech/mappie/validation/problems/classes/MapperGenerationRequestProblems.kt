package tech.mappie.validation.problems.classes

import tech.mappie.resolving.ClassMappingRequest
import tech.mappie.validation.Problem

class MapperGenerationRequestProblems(val mapping: ClassMappingRequest) {

    fun all(): List<Problem> = emptyList()

    companion object {
        fun of(mapping: ClassMappingRequest): MapperGenerationRequestProblems =
            MapperGenerationRequestProblems(mapping)
    }
}