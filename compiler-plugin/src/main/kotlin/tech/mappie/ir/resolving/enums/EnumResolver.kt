package tech.mappie.ir.resolving.enums

import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.statements
import tech.mappie.ir.resolving.MappingResolver
import tech.mappie.ir.resolving.MappingStatementsFinder
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
            .apply {
                val expression = function?.body?.accept(MappingStatementsFinder, Unit)
                expression?.function?.body?.statements?.forEach { statement ->
                    statement.accept(EnumMappingStatementCollector, Unit)
                        ?.let { explicit(it) }
                }
            }
            .construct(context.origin)
            .let { listOf(it) }
}

