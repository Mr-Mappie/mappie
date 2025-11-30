package tech.mappie.ir.resolving.enums

import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.statements
import tech.mappie.ir.MappieContext
import tech.mappie.ir.InternalMappieDefinition
import tech.mappie.ir.MappieDefinition
import tech.mappie.ir.resolving.EnumMappingRequest
import tech.mappie.ir.resolving.MappingPriority
import tech.mappie.ir.resolving.MappingResolver
import tech.mappie.ir.resolving.SuperCallEnumMappings
import tech.mappie.ir.resolving.findMappingStatements

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
        return if (findMappingStatements(parent.referenceMapFunction()).isNotEmpty()) {
            val source = definition.source
            val target = definition.target
            return EnumMappingRequestBuilder(source, target)
                .sources(source.getClass()!!.accept(EnumEntriesCollector(), Unit))
                .targets(target.getClass()!!.accept(EnumEntriesCollector(), Unit))
                .apply {
                    addFunctionStatements(parent.referenceMapFunction(), MappingPriority.LOW)
                    addFunctionStatements(function, MappingPriority.HIGH)
                }
                .construct(origin)
                .let { listOf(it) }
        } else {
            listOf(EnumMappingRequest(origin, definition.source, definition.target, SuperCallEnumMappings()))
        }
    }

    private fun EnumMappingRequestBuilder.addFunctionStatements(function: IrFunction?, priority: MappingPriority) {
        val mapping = findMappingStatements(function?.body).singleOrNull()?.arguments?.getOrNull(1) as? IrFunctionExpression
        mapping?.function?.body?.statements?.forEach { statement ->
            statement.accept(EnumMappingStatementCollector, Unit)
                ?.let { (source, target) -> explicit(source, target, priority) }
        }
    }
}

