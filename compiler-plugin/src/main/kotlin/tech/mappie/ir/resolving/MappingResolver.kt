package tech.mappie.ir.resolving

import org.jetbrains.kotlin.ir.declarations.IrFunction
import tech.mappie.ir.InternalMappieDefinition
import tech.mappie.ir.MappieContext

fun interface MappingResolver {

    context(context: MappieContext)
    fun resolve(origin: InternalMappieDefinition, function: IrFunction?): List<MappingRequest>
}
