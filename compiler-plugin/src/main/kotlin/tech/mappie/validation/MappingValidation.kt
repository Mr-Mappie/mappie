package tech.mappie.validation

import tech.mappie.MappieIrRegistrar.Companion.context
import tech.mappie.resolving.ConstructorCallMapping
import tech.mappie.resolving.EnumMapping
import tech.mappie.resolving.Mapping
import org.jetbrains.kotlin.ir.IrFileEntry
import org.jetbrains.kotlin.ir.util.dumpKotlinLike

interface MappingValidation {
    val problems: List<Problem>

    fun isValid(): Boolean =
        problems.none { it.severity == Problem.Severity.ERROR }

    fun warnings(): List<Problem> =
        problems.filter { it.severity == Problem.Severity.WARNING }

    private class ConstructorCallMappingValidation(private val file: IrFileEntry, private val mapping: ConstructorCallMapping) : MappingValidation {

        override val problems: List<Problem> =
            buildList {
                addAll(MultipleSourcesProblems.of(mapping).all())
                addAll(UnsafeTypeAssignmentProblems.of(file, mapping).all())
                addAll(UnsafePlatformAssignmentProblems.of(file, mapping).all())
                addAll(UnknownParameterNameProblems.of(mapping).all())
                addAll(VisibilityProblems.of(mapping).all())
            }
    }

    private class EnumMappingValidation(private val mapping: EnumMapping) : MappingValidation {

        override val problems: List<Problem> =
            if (context.configuration.strictness.enums) {
                mapping.mappings
                    .filter { (_, targets) -> targets.size != 1 }
                    .map { (source, targets) ->
                        Problem.error("Source ${mapping.sourceType.dumpKotlinLike()}.${source.name.asString()} has ${if (targets.isEmpty()) "no target defined" else "multiple targets defined"}")
                    }
            } else {
                emptyList()
            }
    }

    companion object {
        fun of(file: IrFileEntry, mapping: Mapping): MappingValidation =
            when (mapping) {
                is EnumMapping -> EnumMappingValidation(mapping)
                is ConstructorCallMapping -> ConstructorCallMappingValidation(file, mapping)
                else -> object : MappingValidation { override val problems = emptyList<Problem>() }
            }
    }
}
