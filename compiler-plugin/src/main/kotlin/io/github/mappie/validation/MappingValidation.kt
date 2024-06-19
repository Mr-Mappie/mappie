package io.github.mappie.validation

import io.github.mappie.MappieIrRegistrar.Companion.context
import io.github.mappie.printing.pretty
import io.github.mappie.resolving.ConstructorCallMapping
import io.github.mappie.resolving.EnumMapping
import io.github.mappie.resolving.Mapping
import io.github.mappie.resolving.classes.PropertySource
import io.github.mappie.util.isAssignableFrom
import io.github.mappie.util.location
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.ir.IrFileEntry
import org.jetbrains.kotlin.ir.types.getClass

data class Problem(
    val description: String,
    val severity: Severity,
    val location: CompilerMessageLocation?
) {
    enum class Severity { ERROR, WARNING; }

    companion object {
        fun error(description: String, location: CompilerMessageLocation? = null) =
            Problem(description, Severity.ERROR, location)

        fun warning(description: String, location: CompilerMessageLocation? = null) =
            Problem(description, Severity.WARNING, location)

    }
}

interface MappingValidation {
    val problems: List<Problem>

    fun isValid(): Boolean =
        problems.none { it.severity == Problem.Severity.ERROR }

    fun warnings(): List<Problem> =
        problems.filter { it.severity == Problem.Severity.WARNING }

    private class ConstructorCallMappingValidation(private val file: IrFileEntry, private val mapping: ConstructorCallMapping) : MappingValidation {

        override val problems: List<Problem> =
            buildList {
                addAll(
                    mapping.mappings
                        .filter { (_, sources) -> sources.size != 1 }
                        .map { (target, sources) ->
                            Problem.error("Target ${mapping.targetType.getClass()!!.name.asString()}.${target.name.asString()} has ${if (sources.isEmpty()) "no source defined" else "multiple sources defined"}")
                        }
                )

                addAll(
                    mapping.mappings
                        .filter { (_, sources) -> sources.size == 1 }
                        .filter { (target, sources) -> !target.type.isAssignableFrom(sources.single().resolveType()) }
                        .map { (target, sources) -> Problem.error(
                            "Target ${mapping.targetType.getClass()!!.name.asString()}.${target.name.asString()} has type ${target.type.pretty()} which cannot be assigned from type ${sources.single().resolveType().pretty()}",
                            (sources.single() as? PropertySource)?.origin?.let { location(file, it) }
                        ) }
                )

                addAll(
                    mapping.unknowns.map {
                        Problem.error("Parameter ${it.key.asString()} does not occur as a parameter in constructor")
                    }
                )

                if (!mapping.symbol.owner.visibility.isPublicAPI && context.configuration.strictness.visibility) {
                    add(Problem.error("Constructor is not public", location(mapping.symbol.owner)))
                }
            }
    }

    private class EnumMappingValidation(private val mapping: EnumMapping) : MappingValidation {

        override val problems: List<Problem> =
            if (context.configuration.strictness.enums) {
                mapping.mappings
                    .filter { (_, targets) -> targets.size != 1 }
                    .map { (source, targets) ->
                        Problem.error("Source ${mapping.sourceType.getClass()!!.name.asString()}.${source.name.asString()} has ${if (targets.isEmpty()) "no target defined" else "multiple targets defined"}")
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