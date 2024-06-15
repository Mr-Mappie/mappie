package io.github.mappie.generation

import io.github.mappie.resolving.ConstructorCallMapping
import io.github.mappie.resolving.EnumMapping
import io.github.mappie.resolving.Mapping
import io.github.mappie.resolving.SingleValueMapping
import io.github.mappie.resolving.classes.PropertySource

interface MappingSelector {

    fun select(): Mapping

    private class ConstructorMappingSelector(private val mappings: List<ConstructorCallMapping>) : MappingSelector {

        override fun select(): Mapping {
            return selectPrimary() ?: selectLeastResolvedAutomatically()
        }

        private fun selectPrimary(): Mapping? =
            mappings.firstOrNull { it.symbol.owner.isPrimary }

        private fun selectLeastResolvedAutomatically(): Mapping =
            mappings.maxBy { it.mappings.count { (_, sources) -> !(sources.single() as PropertySource).isResolvedAutomatically } }
    }

    companion object {
        fun of(mappings: List<Mapping>): MappingSelector =
            when {
                mappings.all { it is ConstructorCallMapping } -> ConstructorMappingSelector(mappings.map { it as ConstructorCallMapping })
                mappings.all { it is EnumMapping } -> object : MappingSelector { override fun select() = mappings.single() }
                mappings.all { it is SingleValueMapping } -> object : MappingSelector { override fun select() = mappings.single() }
                else -> error("Not all mappings are of the same type. This is a bug.")
            }
    }
}