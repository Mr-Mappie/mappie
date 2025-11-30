package tech.mappie.ir.resolving.enums

import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.types.IrType
import tech.mappie.ir.resolving.EnumMappingRequest
import tech.mappie.ir.InternalMappieDefinition
import tech.mappie.ir.resolving.MappingPriority
import tech.mappie.ir.resolving.SourcesTargetEnumMappings

class EnumMappingRequestBuilder(val source: IrType, val target: IrType) {

    private val explicit = mutableMapOf<IrEnumEntry, List<Pair<EnumMappingTarget, MappingPriority>>>()

    private val targets = mutableListOf<IrEnumEntry>()

    private val sources = mutableListOf<IrEnumEntry>()

    fun construct(origin: InternalMappieDefinition): EnumMappingRequest {
        val mappings = sources.associateWith { source ->
            buildList {
                explicit[source]?.let {
                    addAll(it.filter { it.second == MappingPriority.HIGH }.ifEmpty { it.filter { it.second == MappingPriority.LOW } }.map { it.first })
                }
                addAll(targets.filter { target -> target.name == source.name }.map { ResolvedEnumMappingTarget(it) })
            }
        }
        return EnumMappingRequest(origin, source, target, SourcesTargetEnumMappings(mappings))
    }

    fun explicit(source: IrEnumEntry, target: EnumMappingTarget, priority: MappingPriority) =
        apply { this.explicit.merge(
            source,
            listOf(target to priority),
            List<Pair<EnumMappingTarget, MappingPriority>>::plus)
        }

    fun targets(targets: List<IrEnumEntry>) =
        apply { this.targets.addAll(targets) }

    fun sources(sources: List<IrEnumEntry>) =
        apply { this.sources.addAll(sources) }
}