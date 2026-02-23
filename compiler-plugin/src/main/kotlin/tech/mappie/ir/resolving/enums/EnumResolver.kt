package tech.mappie.ir.resolving.enums

import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.util.statements
import tech.mappie.exceptions.MappiePanicException.Companion.panic
import tech.mappie.exceptions.MappieProblemException.Companion.fail
import tech.mappie.ir.MappieContext
import tech.mappie.ir.InternalMappieDefinition
import tech.mappie.ir.resolving.MappingPriority
import tech.mappie.ir.resolving.MappingResolver
import tech.mappie.ir.resolving.findMappingStatements
import tech.mappie.ir.util.location

class EnumResolver(
    private val source: IrType,
    private val target: IrType,
) : MappingResolver {

    context(context: MappieContext)
    override fun resolve(origin: InternalMappieDefinition, function: IrFunction?) =
        EnumMappingRequestBuilder(source, target)
            .sources(source.getClass()?.accept(EnumEntriesCollector(), Unit)
                ?: context.fail("Failed to resolve class for source type ${source.dumpKotlinLike()}", location(origin.clazz)))
            .targets(
                target.getClass()?.accept(EnumEntriesCollector(), Unit)
                    ?: context.fail("Failed to resolve class for target type ${target.dumpKotlinLike()}", location(origin.clazz)))
            .apply {
                val mapping = findMappingStatements(function?.body).singleOrNull()?.arguments?.getOrNull(1) as? IrFunctionExpression
                mapping?.function?.body?.statements?.forEach { statement ->
                    statement.accept(EnumMappingStatementCollector, Unit)
                        ?.let { (source, target) -> explicit(source, target, MappingPriority.HIGH) }
                }
            }
            .construct(origin)
            .let { listOf(it) }
}

