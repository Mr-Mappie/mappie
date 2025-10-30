package tech.mappie.ir.resolving

import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.util.*
import tech.mappie.ir.InternalMappieDefinition
import tech.mappie.ir.MappieContext
import tech.mappie.ir.util.BaseVisitor
import tech.mappie.ir.util.isMappieMapFunction

class MappingRequestResolver(private val origin: InternalMappieDefinition) : BaseVisitor<List<MappingRequest>, MappieContext>() {

    override fun visitClass(declaration: IrClass, data: MappieContext): List<MappingRequest> =
        declaration.functions.singleOrNull { it.isMappieMapFunction() }?.accept(data) ?: emptyList()

    override fun visitFunction(declaration: IrFunction, data: MappieContext): List<MappingRequest> {
        return if (declaration.accept(ShouldTransformCollector(data), Unit)) {
            context(data) { MappingResolver.of(declaration).resolve(origin, declaration) }
        } else {
            emptyList()
        }
    }
}
