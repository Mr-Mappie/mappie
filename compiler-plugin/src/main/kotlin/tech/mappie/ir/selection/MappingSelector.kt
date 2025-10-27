package tech.mappie.ir.selection

import tech.mappie.exceptions.MappiePanicException.Companion.panic
import tech.mappie.ir.resolving.ClassMappingRequest
import tech.mappie.ir.resolving.EnumMappingRequest
import tech.mappie.ir.resolving.MappingRequest
import tech.mappie.ir.resolving.classes.sources.ExplicitClassMappingSource
import tech.mappie.ir.analysis.ValidationResult

interface MappingSelector {

    fun select(): Pair<MappingRequest?, ValidationResult>?

    private class ConstructorMappingSelector(private val options: Map<ClassMappingRequest, ValidationResult>) : MappingSelector {

        override fun select(): Pair<ClassMappingRequest?, ValidationResult>? =
            primary() ?: secondary()

        private fun primary(): Pair<ClassMappingRequest, ValidationResult>? =
            options.entries.firstOrNull { (request, validation) ->
                request.constructor.isPrimary && validation.isValid
            }?.toPair()

        private fun secondary(): Pair<ClassMappingRequest?, ValidationResult>? =
            options.entries.toList().run {
                val valids = filter { it.value.isValid }
                if (valids.isNotEmpty()) {
                    valids.maxBy { it.key.mappings.values.count { it.single() is ExplicitClassMappingSource } }.toPair()
                } else {
                    minByOrNull { it.value.problems.size }
                        ?.let { (_, validation) -> null to validation }
                }
            }
    }

    private class EnumMappingSelector(private val options: Map<EnumMappingRequest, ValidationResult>) : MappingSelector {
        override fun select(): Pair<MappingRequest?, ValidationResult> =
            options.entries.single().let { (request, validation) ->
                (if (validation.isValid) request else null) to validation
            }
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun of(options: Map<MappingRequest, ValidationResult>): MappingSelector =
            when {
                options.keys.all { it is ClassMappingRequest } -> ConstructorMappingSelector(options as Map<ClassMappingRequest, ValidationResult>)
                options.keys.all { it is EnumMappingRequest } -> EnumMappingSelector(options as Map<EnumMappingRequest, ValidationResult>)
                else -> panic("Not all mappings are of the same type")
            }

//        @Suppress("UNCHECKED_CAST")
//        fun of(options: List<MappingRequest>): MappingSelector =
//            when {
//                options.all { it is ClassMappingRequest } -> ConstructorMappingSelector(
//                    options.associateWith { MappingValidation.valid() } as Map<ClassMappingRequest, MappingValidation>
//                )
//                options.all { it is EnumMappingRequest } -> EnumMappingSelector(
//                    options.associateWith { MappingValidation.valid() } as Map<EnumMappingRequest, MappingValidation>
//                )
//                else -> panic("Not all mappings are of the same type")
//            }

    }
}