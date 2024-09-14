package tech.mappie.resolving.enums

import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.types.IrType
import tech.mappie.resolving.EnumMappingRequest

class EnumMappingRequestBuilder(val source: IrType, val target: IrType) {

    private val explicit = mutableMapOf<IrEnumEntry, List<EnumMappingTarget>>()

    private val targets = mutableListOf<IrEnumEntry>()

    private val sources = mutableListOf<IrEnumEntry>()

    fun construct(origin: IrFunction): EnumMappingRequest {
        val mappings = sources.associateWith { source ->
            buildList {
                explicit[source]?.let { addAll(it) }
                addAll(targets.filter { target -> target.name == source.name }.map { ResolvedEnumMappingTarget(it) })
            }
        }
        return EnumMappingRequest(origin, source, target, mappings)
    }

    fun explicit(entry: Pair<IrEnumEntry, EnumMappingTarget>) =
        apply { explicit.merge(entry.first, listOf(entry.second), List<EnumMappingTarget>::plus) }

    fun targets(targets: List<IrEnumEntry>) =
        apply { this.targets.addAll(targets) }

    fun sources(sources: List<IrEnumEntry>) =
        apply { this.sources.addAll(sources) }
}