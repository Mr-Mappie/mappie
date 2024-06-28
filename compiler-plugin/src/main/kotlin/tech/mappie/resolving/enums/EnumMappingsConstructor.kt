package tech.mappie.resolving.enums

import tech.mappie.resolving.EnumMapping
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.types.IrType

class EnumMappingsConstructor private constructor(val targetType: IrType, val sourceType: IrType) {

    val explicit = mutableMapOf<IrEnumEntry, List<EnumMappingTarget>>()

    val targets = mutableListOf<IrEnumEntry>()

    val sources = mutableListOf<IrEnumEntry>()

    fun construct(): EnumMapping {
        val mappings = sources.associateWith { source ->
            val resolved = targets.filter { target -> target.name == source.name }
            val explicit = explicit[source]
            explicit ?: resolved.map { ResolvedEnumMappingTarget(it) }
        }
        return EnumMapping(targetType, sourceType, mappings)
    }

    fun explicit(entry: Pair<IrEnumEntry, EnumMappingTarget>): EnumMappingsConstructor =
        apply { explicit.merge(entry.first, listOf(entry.second), Collection<EnumMappingTarget>::plus) }

    companion object {
        fun of(target: IrType, source: IrType) =
            EnumMappingsConstructor(target, source)
    }
}