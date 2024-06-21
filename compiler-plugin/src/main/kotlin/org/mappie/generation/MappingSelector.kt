package org.mappie.generation

import org.mappie.resolving.ConstructorCallMapping
import org.mappie.resolving.EnumMapping
import org.mappie.resolving.Mapping
import org.mappie.resolving.SingleValueMapping
import org.mappie.resolving.classes.PropertySource
import org.mappie.validation.MappingValidation

interface MappingSelector {

    fun select(): Pair<Mapping, MappingValidation>

    private class ConstructorMappingSelector(private val mappings: List<Pair<ConstructorCallMapping, MappingValidation>>) : MappingSelector {

        override fun select(): Pair<Mapping, MappingValidation> {
            return selectPrimary() ?: selectLeastResolvedAutomatically()
        }

        private fun selectPrimary(): Pair<Mapping, MappingValidation>? =
            mappings.firstOrNull { it.first.symbol.owner.isPrimary }

        private fun selectLeastResolvedAutomatically(): Pair<Mapping, MappingValidation> =
            mappings.maxBy { it.first.mappings.count { (_, sources) -> !(sources.single() as PropertySource).isResolvedAutomatically } }
    }

    companion object {
        fun of(mappings: List<Pair<Mapping, MappingValidation>>): MappingSelector =
            when {
                mappings.all { it.first is ConstructorCallMapping } -> ConstructorMappingSelector(mappings.map { it.first as ConstructorCallMapping to it.second })
                mappings.all { it.first is EnumMapping } -> object : MappingSelector { override fun select() = mappings.single() }
                mappings.all { it.first is SingleValueMapping } -> object : MappingSelector { override fun select() = mappings.single() }
                else -> error("Not all mappings are of the same type. This is a bug.")
            }
    }
}