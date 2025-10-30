package tech.mappie.ir.resolving.classes

import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import tech.mappie.ir.resolving.*
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.statements
import org.jetbrains.kotlin.name.Name
import tech.mappie.ir.InternalMappieDefinition
import tech.mappie.ir.MappieContext
import tech.mappie.ir.resolving.classes.targets.MappieTargetsCollector

class ClassResolver(
    private val sources: List<Pair<Name, IrType>>,
    private val target: IrType,
) : MappingResolver {

    context(context: MappieContext)
    override fun resolve(origin: InternalMappieDefinition, function: IrFunction?): List<ClassMappingRequest> =
        target.getClass()!!.constructors.map { constructor ->
            ClassMappingRequestBuilder(constructor)
                .targets(MappieTargetsCollector(target, function, constructor).collect())
                .sources(sources)
                .apply {
                    val mapping = findMappingStatements(function?.body).singleOrNull()?.arguments?.getOrNull(1) as? IrFunctionExpression
                    mapping?.function?.body?.statements?.forEach { statement ->
                        statement.accept(ClassMappingStatementCollector(), context)
                            ?.let { explicit(it) }
                    }
                }
                .construct(origin)
        }.toList()
}