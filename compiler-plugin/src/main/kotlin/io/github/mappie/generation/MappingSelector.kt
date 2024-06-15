package io.github.mappie.generation

import io.github.mappie.resolving.ConstructorCallMapping
import io.github.mappie.resolving.EnumMapping
import io.github.mappie.resolving.Mapping
import io.github.mappie.resolving.SingleValueMapping
import io.github.mappie.resolving.classes.PropertySource
import io.github.mappie.validation.MappingValidation

interface MappingSelector {

    fun select(): Mapping

    private class ConstructorMappingSelector(
        private val mappings: List<Pair<ConstructorCallMapping, MappingValidation>>
    ) : MappingSelector {

        override fun select(): Mapping {
            return selectPrimary() ?: selectLeastResolvedAutomatically()
        }

        private fun selectPrimary(): Mapping? =
            mappings.firstOrNull { it.first.symbol.owner.isPrimary }?.first

        private fun selectLeastResolvedAutomatically(): Mapping =
            mappings
                .maxBy { it.first.mappings.count { (_, sources) -> !(sources.single() as PropertySource).isResolvedAutomatically } }
                .first
    }

    companion object {
        fun of(mappings: List<Pair<Mapping, MappingValidation>>): MappingSelector =
            when {
                mappings.all { it.first is ConstructorCallMapping } -> ConstructorMappingSelector(mappings.map { it.first as ConstructorCallMapping to it.second })
                mappings.all { it.first is EnumMapping } -> object : MappingSelector { override fun select() = mappings.first().first }
                mappings.all { it.first is SingleValueMapping } -> object : MappingSelector { override fun select() = mappings.first().first }
                else -> error("Not all mappings are of the same type. This is a bug.")
            }
    }
}