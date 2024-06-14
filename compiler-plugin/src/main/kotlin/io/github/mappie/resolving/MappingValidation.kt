package io.github.mappie.resolving

import io.github.mappie.MappieIrRegistrar.Companion.context
import org.jetbrains.kotlin.ir.types.getClass

interface MappingValidation {
    fun isValid(): Boolean =
        problems().isEmpty()

    fun problems(): List<String>

    private class ConstructorCallMappingValidation(private val mapping: ConstructorCallMapping) : MappingValidation {

        override fun problems(): List<String> =
            mapping.mappings
                .filter { (_, sources) -> sources.size != 1 }
                .map { (target, sources) -> "Target ${mapping.targetType.getClass()!!.name.asString()}.${target.name.asString()} has ${if (sources.isEmpty()) "no source defined" else "multiple sources defined"}" }
    }

    private class EnumMappingValidation(private val mapping: EnumMapping) : MappingValidation {

        override fun problems(): List<String> =
            if (context.configuration.strictness.enums) {
                mapping.mappings
                    .filter { (_, targets) -> targets.size != 1 }
                    .map { (source, targets) -> "Source ${mapping.sourceType.getClass()!!.name.asString()}.${source.name.asString()} has ${if (targets.isEmpty()) "no target defined" else "multiple targets defined"}" }
            } else {
                emptyList()
            }
    }

    companion object {
        fun of(mapping: Mapping): MappingValidation =
            when (mapping) {
                is EnumMapping -> EnumMappingValidation(mapping)
                is ConstructorCallMapping -> ConstructorCallMappingValidation(mapping)
                else -> object : MappingValidation { override fun isValid(): Boolean = true; override fun problems() = emptyList<String>() }
            }
    }
}