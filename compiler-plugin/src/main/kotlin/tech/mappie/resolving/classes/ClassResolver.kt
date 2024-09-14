package tech.mappie.resolving.classes

import tech.mappie.resolving.*
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.constructors
import tech.mappie.resolving.classes.sources.ImplicitClassMappingSourcesCollector
import tech.mappie.resolving.classes.targets.MappieTargetsCollector

class ClassResolver(private val declaration: IrFunction, private val context: ResolverContext) : MappingResolver {

    override fun resolve(): List<ClassMappingRequest> =
        declaration.returnType.getClass()!!.constructors.map { constructor ->
            ClassMappingRequestBuilder(constructor, context)
                .targets(MappieTargetsCollector(constructor).collect())
                .apply { declaration.valueParameters.map { sources(it.accept(ImplicitClassMappingSourcesCollector(), it)) } }
                .also { declaration.body?.accept(ExplicitClassMappingCollector(context), it) }
                .construct(declaration)
        }.toList()
}