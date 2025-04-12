package tech.mappie.ir.resolving.enums

import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.getClass
import tech.mappie.ir.resolving.MappingResolver
import tech.mappie.ir.resolving.ResolverContext

class EnumResolver(
    private val context: ResolverContext,
    private val source: IrType,
    private val target: IrType,
) : MappingResolver {

    override fun resolve(function: IrFunction?) =
        EnumMappingRequestBuilder(source, target)
            .sources(source.getClass()!!.accept(EnumEntriesCollector(), Unit))
            .targets(target.getClass()!!.accept(EnumEntriesCollector(), Unit))
            .also { function?.body?.accept(EnumMappingBodyCollector(), it) }
            .construct(context.function!!)
            .let { listOf(it) }
}

