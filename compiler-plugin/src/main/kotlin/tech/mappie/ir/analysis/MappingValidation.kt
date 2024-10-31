package tech.mappie.ir.analysis

import tech.mappie.ir.analysis.problems.classes.MapperGenerationRequestProblems
import tech.mappie.ir.analysis.problems.classes.MultipleSourcesProblems
import tech.mappie.ir.analysis.problems.classes.UnsafePlatformTypeAssignmentProblems
import tech.mappie.ir.analysis.problems.classes.UnsafeTypeAssignmentProblems
import tech.mappie.ir.analysis.problems.classes.VisibilityProblems
import tech.mappie.ir.analysis.problems.enums.AllSourcesMappedProblems
import tech.mappie.ir.analysis.problems.enums.UnnecessaryExplicitMappingProblems
import tech.mappie.ir.resolving.ClassMappingRequest
import tech.mappie.ir.resolving.EnumMappingRequest
import tech.mappie.ir.resolving.MappingRequest
import tech.mappie.ir.analysis.problems.classes.UnknownParameterNameProblems
import tech.mappie.ir.analysis.problems.classes.ClassConfigProblems
import tech.mappie.ir.analysis.problems.enums.EnumConfigProblems

interface MappingValidation {
    val problems: List<Problem>

    fun isValid() = problems.none { it.severity == Problem.Severity.ERROR }

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
                addAll(ClassConfigProblems.of(context, mapping).all())
            }
    }

    private class EnumMappingRequestValidation(
        private val context: ValidationContext,
        private val mapping: EnumMappingRequest,
    ) : MappingValidation {
        override val problems: List<Problem> = buildList {
            addAll(UnnecessaryExplicitMappingProblems.of(context, mapping).all())
            addAll(AllSourcesMappedProblems.of(context, mapping).all())
            addAll(EnumConfigProblems.of(context, mapping).all())
        }
    }

    private class IdentityValidation : MappingValidation {
        override val problems = emptyList<Problem>()
    }

    companion object {
        fun of(context: ValidationContext, mapping: MappingRequest): MappingValidation =
            when (mapping) {
                is EnumMappingRequest -> EnumMappingRequestValidation(context, mapping)
                is ClassMappingRequest -> ClassMappingRequestValidation(context, mapping)
            }

        fun valid(): MappingValidation =
            IdentityValidation()
    }
}
