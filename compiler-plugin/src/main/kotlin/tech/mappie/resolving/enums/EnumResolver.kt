package tech.mappie.resolving.enums

import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.types.getClass
import tech.mappie.resolving.MappingResolver
import tech.mappie.resolving.ResolverContext

class EnumResolver(private val declaration: IrFunction, private val context: ResolverContext) : MappingResolver {

    private val source = declaration.valueParameters.first().type

    private val target = declaration.returnType

    override fun resolve() =
        EnumMappingRequestBuilder(source, target)
            .sources(source.getClass()!!.accept(EnumEntriesCollector(), Unit))
            .targets(target.getClass()!!.accept(EnumEntriesCollector(), Unit))
            .also { declaration.body?.accept(EnumMappingBodyCollector(), it) }
            .construct(declaration)
            .let { listOf(it) }
}

