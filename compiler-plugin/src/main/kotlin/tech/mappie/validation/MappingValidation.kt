package tech.mappie.validation

import tech.mappie.resolving.*
import tech.mappie.validation.problems.classes.*
import tech.mappie.validation.problems.enums.AllSourcesMappedProblems
import tech.mappie.validation.problems.enums.UnnecessaryExplicitMappingProblems

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
                addAll(UnknownParameterNameProblems.of(context, mapping).all())
                addAll(VisibilityProblems.of(context, mapping).all())
                addAll(MapperGenerationRequestProblems.of(context, mapping).all())
                addAll(UnnecessaryFromPropertyNotNullProblems.of(context, mapping).all())
                addAll(CompileTimeReceiverDslProblems.of(context, mapping).all())
            }
    }

    private class EnumMappingRequestValidation(
        private val context: ValidationContext,
        private val mapping: EnumMappingRequest,
    ) : MappingValidation {

        override val problems: List<Problem> = buildList {
            addAll(UnnecessaryExplicitMappingProblems.of(context, mapping).all())
            addAll(AllSourcesMappedProblems.of(context, mapping).all())
        }
    }

    companion object {
        fun of(context: ValidationContext, mapping: MappingRequest): MappingValidation =
            when (mapping) {
                is EnumMappingRequest -> EnumMappingRequestValidation(context, mapping)
                is ClassMappingRequest -> ClassMappingRequestValidation(context, mapping)
            }

        fun valid(): MappingValidation =
            object : MappingValidation {
                override val problems: List<Problem> = emptyList()
            }
    }
}
