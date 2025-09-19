package tech.mappie.ir.resolving.classes

import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import tech.mappie.ir.resolving.*
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.statements
import org.jetbrains.kotlin.name.Name
import tech.mappie.ir.resolving.classes.targets.MappieTargetsCollector

class ClassResolver(
    private val context: ResolverContext,
    private val sources: List<Pair<Name, IrType>>,
    private val target: IrType,
) : MappingResolver {

    override fun resolve(function: IrFunction?): List<ClassMappingRequest> =
        target.getClass()!!.constructors.map { constructor ->
            ClassMappingRequestBuilder(constructor, context)
                .targets(MappieTargetsCollector(target, function, constructor).collect())
                .sources(sources)
                .apply {
                    val mapping = findMappingStatements(function?.body).singleOrNull()?.arguments?.getOrNull(1) as? IrFunctionExpression
                    mapping?.function?.body?.statements?.forEach { statement ->
                        statement.accept(ClassMappingStatementCollector(context), Unit)
                            ?.let { explicit(it) }
                    }
                }
                .construct()
        }.toList()
}