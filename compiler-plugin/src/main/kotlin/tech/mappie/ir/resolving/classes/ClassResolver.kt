package tech.mappie.ir.resolving.classes

import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionReference
import org.jetbrains.kotlin.ir.symbols.IrConstructorSymbol
import tech.mappie.ir.resolving.*
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.statements
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstanceOrNull
import tech.mappie.ir.InternalMappieDefinition
import tech.mappie.ir.MappieContext
import tech.mappie.ir.resolving.classes.targets.MappieTargetsCollector

class ClassResolver(
    private val sources: List<Pair<Name, IrType>>,
    private val target: IrType,
) : MappingResolver {

    context(context: MappieContext)
    override fun resolve(origin: InternalMappieDefinition, function: IrFunction?): List<ClassMappingRequest> {
        val mapping = findMappingStatements(function?.body).singleOrNull()
        return constructors(mapping).map { constructor ->
            ClassMappingRequestBuilder(constructor)
                .targets(MappieTargetsCollector(target, function, constructor).collect())
                .sources(sources)
                .apply {
                    mapping?.arguments?.firstIsInstanceOrNull<IrFunctionExpression>()?.function?.body?.statements?.forEach { statement ->
                        statement.accept(ClassMappingStatementCollector(origin), context)
                            ?.let { explicit(it) }
                    }
                }
                .construct(origin)
        }.toList()
    }

    private fun constructors(call: IrCall?): Sequence<IrConstructor> {
        return if (call == null || call.arguments.size == 2) {
            target.getClass()!!.constructors
        } else {
            sequenceOf(((call.arguments[1] as IrFunctionReference).symbol as IrConstructorSymbol).owner)
        }
    }
}
