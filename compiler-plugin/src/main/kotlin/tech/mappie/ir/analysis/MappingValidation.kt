package tech.mappie.ir.analysis

import tech.mappie.ir.resolving.*
import tech.mappie.ir.analysis.problems.classes.*
import tech.mappie.ir.analysis.problems.enums.AllSourcesMappedProblems

interface MappingValidation {
    val problems: List<Problem>

    fun isValid(): Boolean =
        problems.none { it.severity == Problem.Severity.ERROR }

    fun errors(): List<Problem> =
        problems.filter { it.severity == Problem.Severity.ERROR }

    fun warnings(): List<Problem> =
        problems.filter { it.severity == Problem.Severity.WARNING }

    private class ClassMappingRequestValidation(
        private val context: ValidationContext,
        private val mapping: ClassMappingRequest,
    ) : MappingValidation {

        override val problems: List<Problem> =
            buildList {
                addAll(MultipleSourcesProblems.of(mapping).all())
                addAll(UnsafeTypeAssignmentProblems.of(context, mapping).all())
                addAll(UnsafePlatformTypeAssignmentProblems.of(context, mapping).all())
                addAll(VisibilityProblems.of(context, mapping).all())
                addAll(MapperGenerationRequestProblems.of(context, mapping).all())
                addAll(UnnecessaryFromPropertyNotNullProblems.of(context, mapping).all())
                addAll(EnumConstructionProblems.of(context, mapping).all())
            }
    }

    private class ClassUpdateRequestValidation(
        private val context: ValidationContext,
        private val mapping: ClassUpdateRequest,
    ) : MappingValidation {

        override val problems: List<Problem> =
            buildList {
                addAll(MultipleSourcesProblems.of(mapping).all())
                addAll(UnsafeTypeAssignmentProblems.of(context, mapping).all())
                addAll(UnsafePlatformTypeAssignmentProblems.of(context, mapping).all())
                addAll(MapperGenerationRequestProblems.of(context, mapping).all())
                addAll(UnnecessaryFromPropertyNotNullProblems.of(context, mapping).all())
                addAll(EnumConstructionProblems.of(context, mapping).all())
            }
    }

    private class EnumMappingRequestValidation(
        private val context: ValidationContext,
        private val mapping: EnumMappingRequest,
    ) : MappingValidation {

        override val problems: List<Problem> = buildList {
            addAll(AllSourcesMappedProblems.of(context, mapping).all())
        }
    }

    companion object {
        fun of(context: ValidationContext, mapping: MappingRequest): MappingValidation =
            when (mapping) {
                is EnumMappingRequest -> EnumMappingRequestValidation(context, mapping)
                is ClassMappingRequest -> ClassMappingRequestValidation(context, mapping)
                is ClassUpdateRequest -> ClassUpdateRequestValidation(context, mapping)
            }

        fun valid(): MappingValidation =
            object : MappingValidation {
                override val problems: List<Problem> = emptyList()
            }
    }
}
