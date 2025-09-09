package tech.mappie.ir.resolving

import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.util.*
import tech.mappie.MappieContext
import tech.mappie.ir.util.BaseVisitor
import tech.mappie.ir.util.isMappieMapFunction
import tech.mappie.shouldGenerateCode
import tech.mappie.util.merge

class RequestResolverContext(context: MappieContext, val definitions: List<MappieDefinition>) : MappieContext by context

class MappingRequestResolver : BaseVisitor<Map<IrClass, List<MappingRequest>>, RequestResolverContext>() {

    override fun visitModuleFragment(declaration: IrModuleFragment, data: RequestResolverContext) =
        declaration.files.map { file -> file.accept(data) }.merge()

    override fun visitFile(declaration: IrFile, data: RequestResolverContext) =
        declaration.declarations
            .filterIsInstance<IrClass>()
            .map { it.accept(data) }
            .merge()

    override fun visitClass(declaration: IrClass, data: RequestResolverContext) =
        buildList {
            addAll(declaration.declarations.filterIsInstance<IrClass>().map { it.accept(data) })
            if (data.shouldGenerateCode(declaration)) {
                val result = declaration.functions
                    .singleOrNull { it.isMappieMapFunction() }
                    ?.accept(data)

                if (result != null) {
                    add(result)
                }
            }
        }.merge()

    override fun visitFunction(declaration: IrFunction, data: RequestResolverContext): Map<IrClass, List<MappingRequest>> {
//        if (declaration.accept(ShouldTransformCollector(data), Unit)) {
        val request = MappingResolver.of(declaration, ResolverContext(data, data.definitions, declaration)).resolve(declaration)
        return mapOf(declaration.parentAsClass to request)
//        } else {
//            emptyMap()
//        }
    }
}