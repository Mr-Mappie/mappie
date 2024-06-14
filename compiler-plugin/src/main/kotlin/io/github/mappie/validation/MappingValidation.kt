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

data class Problem(val description: String, val location: CompilerMessageLocation?)

interface MappingValidation {
    fun isValid(): Boolean =
        problems().isEmpty()

    fun problems(): List<Problem>

    private class ConstructorCallMappingValidation(private val file: IrFileEntry, private val mapping: ConstructorCallMapping) : MappingValidation {

        override fun problems(): List<Problem> =
            buildList {
                addAll(
                    mapping.mappings
                        .filter { (_, sources) -> sources.size != 1 }
                        .map { (target, sources) -> Problem(
                            "Target ${mapping.targetType.getClass()!!.name.asString()}.${target.name.asString()} has ${if (sources.isEmpty()) "no source defined" else "multiple sources defined"}",
                            null,
                        ) }
                )

                addAll(
                    mapping.mappings
                        .filter { (_, sources) -> sources.size == 1 }
                        .filter { (target, sources) -> !target.type.isAssignableFrom(sources.single().resolveType()) }
                        .map { (target, sources) -> Problem(
                            "Target ${mapping.targetType.getClass()!!.name.asString()}.${target.name.asString()} has type ${target.type.pretty()} which cannot be assigned from type ${sources.single().resolveType().pretty()}",
                            (sources.single() as? PropertySource)?.origin?.let { location(file, it) }
                        ) }
                )
            }
    }

    private class EnumMappingValidation(private val mapping: EnumMapping) : MappingValidation {

        override fun problems(): List<Problem> =
            if (context.configuration.strictness.enums) {
                mapping.mappings
                    .filter { (_, targets) -> targets.size != 1 }
                    .map { (source, targets) -> Problem(
                        "Source ${mapping.sourceType.getClass()!!.name.asString()}.${source.name.asString()} has ${if (targets.isEmpty()) "no target defined" else "multiple targets defined"}",
                        null,
                    ) }
            } else {
                emptyList()
            }
    }

    companion object {
        fun of(file: IrFileEntry, mapping: Mapping): MappingValidation =
            when (mapping) {
                is EnumMapping -> EnumMappingValidation(mapping)
                is ConstructorCallMapping -> ConstructorCallMappingValidation(file, mapping)
                else -> object : MappingValidation { override fun isValid(): Boolean = true; override fun problems() = emptyList<Problem>() }
            }
    }
}