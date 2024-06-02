package io.github.stefankoppier.mapping.resolving

import org.jetbrains.kotlin.ir.types.getClass

interface MappingValidation {
    fun isValid(): Boolean

    fun problems(): List<String>

    private class EnumMappingValidation(private val mapping: EnumMapping) : MappingValidation {

        override fun isValid() =
            mapping.mappings.all { (_, sources) -> sources.size == 1 }

        override fun problems(): List<String> =
            mapping.mappings
                .filter { (_, sources) -> sources.size != 1 }
                .map { (target, sources) -> "Target ${mapping.targetType.getClass()!!.name.asString()}.${target.name.asString()} has ${if (sources.isEmpty()) "no source defined" else "multiple sources defined"}" }
    }

    companion object {
        fun of(mapping: Mapping): MappingValidation =
            when (mapping) {
                is EnumMapping -> EnumMappingValidation(mapping)
                else -> object : MappingValidation { override fun isValid(): Boolean = true; override fun problems() = emptyList<String>() }
            }
    }
}