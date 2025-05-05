package tech.mappie.ir.resolving.classes.updating

import org.jetbrains.kotlin.ir.declarations.IrFunction
import tech.mappie.ir.resolving.*
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.primaryConstructor
import org.jetbrains.kotlin.name.Name
import tech.mappie.ir.resolving.classes.ExplicitClassMappingCollector
import tech.mappie.ir.resolving.classes.targets.MappieTargetsCollector

class ClassUpdateResolver(
    private val context: ResolverContext,
    private val source: Pair<Name, IrType>,
    private val updater: Pair<Name, IrType>,
) : MappingResolver {

    override fun resolve(function: IrFunction?): List<ClassUpdateRequest> =
        listOf(
            ClassUpdateRequestBuilder(context)
                .targets(MappieTargetsCollector(function, source.second.getClass()!!.primaryConstructor!!).some())
                .updater(updater)
                .also { function?.body?.accept(ExplicitClassMappingCollector(context), it) }
                .construct(context.function!!)
        )
}