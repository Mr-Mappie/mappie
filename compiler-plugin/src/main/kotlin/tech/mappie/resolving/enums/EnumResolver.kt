package tech.mappie.resolving.enums

import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.getClass
import tech.mappie.resolving.MappingResolver
import tech.mappie.resolving.ResolverContext

class EnumResolver(
    private val context: ResolverContext,
    private val source: IrType,
    private val target: IrType,
) : MappingResolver {

    override fun resolve(body: IrBody?) =
        EnumMappingRequestBuilder(source, target)
            .sources(source.getClass()!!.accept(EnumEntriesCollector(), Unit))
            .targets(target.getClass()!!.accept(EnumEntriesCollector(), Unit))
            .also { body?.accept(EnumMappingBodyCollector(), it) }
            .construct(context.function!!)
            .let { listOf(it) }
}

