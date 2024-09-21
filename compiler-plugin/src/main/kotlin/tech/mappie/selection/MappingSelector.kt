package tech.mappie.selection

import tech.mappie.exceptions.MappiePanicException
import tech.mappie.resolving.ClassMappingRequest
import tech.mappie.resolving.EnumMappingRequest
import tech.mappie.resolving.MappingRequest
import tech.mappie.validation.MappingValidation

interface MappingSelector {

    fun select(): Pair<MappingRequest?, MappingValidation>?

    private class ConstructorMappingSelector(private val options: Map<ClassMappingRequest, MappingValidation>) : MappingSelector {

        override fun select(): Pair<ClassMappingRequest?, MappingValidation>? =
            primary() ?: secondary()

        private fun primary(): Pair<ClassMappingRequest, MappingValidation>? =
            options.entries.firstOrNull { (request, validation) ->
                request.constructor.isPrimary && validation.isValid()
            }?.toPair()

        private fun secondary(): Pair<ClassMappingRequest?, MappingValidation>? =
            options.entries.run {
                firstOrNull { it.value.isValid() }?.toPair() // TODO: select one with most explicit mappings
                    ?: minByOrNull { it.value.problems.size }?.let { (_, validation) ->
                        null to validation
                    }
            }
    }

    private class EnumMappingSelector(private val options: Map<EnumMappingRequest, MappingValidation>) : MappingSelector {
        override fun select(): Pair<MappingRequest?, MappingValidation> =
            options.entries.single().let { (request, validation) ->
                (if (validation.isValid()) request else null) to validation
            }
    }

    companion object {
        @Suppress("UNCHECKED_CAST")
        fun of(options: Map<MappingRequest, MappingValidation>): MappingSelector =
            when {
                options.keys.all { it is ClassMappingRequest } -> ConstructorMappingSelector(options as Map<ClassMappingRequest, MappingValidation>)
                options.keys.all { it is EnumMappingRequest } -> EnumMappingSelector(options as Map<EnumMappingRequest, MappingValidation>)
                else -> throw MappiePanicException("Not all mappings are of the same type")
            }

        @Suppress("UNCHECKED_CAST")
        fun of(options: List<MappingRequest>): MappingSelector =
            when {
                options.all { it is ClassMappingRequest } -> ConstructorMappingSelector(
                    options.associateWith { MappingValidation.valid() } as Map<ClassMappingRequest, MappingValidation>
                )
                options.all { it is EnumMappingRequest } -> EnumMappingSelector(
                    options.associateWith { MappingValidation.valid() } as Map<EnumMappingRequest, MappingValidation>
                )
                else -> throw MappiePanicException("Not all mappings are of the same type")
            }

    }
}