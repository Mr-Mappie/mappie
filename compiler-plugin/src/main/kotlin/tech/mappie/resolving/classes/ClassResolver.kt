package tech.mappie.resolving.classes

import tech.mappie.resolving.*
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.name.Name
import tech.mappie.resolving.classes.sources.ImplicitClassMappingSourcesCollector
import tech.mappie.resolving.classes.targets.MappieTargetsCollector

class ClassResolver(
    private val context: ResolverContext,
    private val sources: List<Pair<Name, IrType>>,
    private val target: IrType,
) : MappingResolver {

    override fun resolve(body: IrBody?): List<ClassMappingRequest> =
        target.getClass()!!.constructors.map { constructor ->
            ClassMappingRequestBuilder(constructor, context)
                .targets(MappieTargetsCollector(constructor).collect())
                .sources(sources)
                .also { body?.accept(ExplicitClassMappingCollector(context), it) }
                .construct(context.function!!)
        }.toList()
}