package tech.mappie.ir.resolving.enums

import org.jetbrains.kotlin.ir.declarations.IrFunction
import tech.mappie.ir.MappieContext
import tech.mappie.ir.InternalMappieDefinition
import tech.mappie.ir.MappieDefinition
import tech.mappie.ir.resolving.EnumMappingRequest
import tech.mappie.ir.resolving.MappingResolver
import tech.mappie.ir.resolving.SuperCallEnumMappings

class InheritedEnumResolver(
    private val definition: InternalMappieDefinition,
) : MappingResolver {

    val parent: MappieDefinition

    init {
        requireNotNull(definition.parent)
        this.parent = definition.parent
    }

    context(context: MappieContext)
    override fun resolve(origin: InternalMappieDefinition, function: IrFunction?): List<EnumMappingRequest> {
        return listOf(EnumMappingRequest(origin, definition.source, definition.target, SuperCallEnumMappings()))
    }
}

