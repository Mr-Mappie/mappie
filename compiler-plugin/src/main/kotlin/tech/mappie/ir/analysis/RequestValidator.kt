package tech.mappie.ir.analysis

import tech.mappie.ir.MappieContext
import tech.mappie.ir.resolving.*
import tech.mappie.ir.analysis.problems.classes.*
import tech.mappie.ir.analysis.problems.enums.AllSourcesMappedProblems

data class ValidationResult(val problems: List<Problem>) {
    val errors = problems.filter { it.severity == Problem.Severity.ERROR }
    val warnings = problems.filter { it.severity == Problem.Severity.WARNING }

    val isValid = errors.isEmpty()
}

interface RequestValidator {

    context(context: MappieContext)
    fun evaluate(): ValidationResult

    private class ClassRequestValidator(private val mapping: ClassMappingRequest) : RequestValidator {

        context(context: MappieContext)
        override fun evaluate() = ValidationResult(buildList {
            addAll(MultipleSourcesProblems.of(mapping).all())
            addAll(UnsafeTypeAssignmentProblems.of(mapping).all())
            addAll(UnsafePlatformTypeAssignmentProblems.of(mapping).all())
            addAll(VisibilityProblems.of(mapping).all())
//            addAll(MapperGenerationRequestProblems.of(mapping).all())
            addAll(UnnecessaryFromPropertyNotNullProblems.of(mapping).all())
            addAll(EnumConstructionProblems.of(mapping).all())
        })
    }

    private class EnumRequestValidator(private val mapping: EnumMappingRequest) : RequestValidator {

        context(context: MappieContext)
        override fun evaluate() = ValidationResult(buildList {
            addAll(AllSourcesMappedProblems.of(mapping).all())
        })
    }

    companion object {
        context(context: MappieContext)
        fun of(mapping: MappingRequest): RequestValidator =
            when (mapping) {
                is EnumMappingRequest -> EnumRequestValidator(mapping)
                is ClassMappingRequest -> ClassRequestValidator(mapping)
            }

        fun valid(): RequestValidator = object : RequestValidator {
            context(context: MappieContext)
            override fun evaluate() = ValidationResult(emptyList())
        }
    }
}
