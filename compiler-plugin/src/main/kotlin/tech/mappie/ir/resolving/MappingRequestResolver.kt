package tech.mappie.ir.resolving

import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.util.*
import tech.mappie.ir.util.BaseVisitor
import tech.mappie.shouldGenerateCode
import tech.mappie.util.merge

class MappingRequestResolver : BaseVisitor<Map<IrClass, List<MappingRequest>>, ResolverContext>() {

    override fun visitModuleFragment(declaration: IrModuleFragment, data: ResolverContext) =
        declaration.files.map { file -> file.accept(data) }.merge()

    override fun visitFile(declaration: IrFile, data: ResolverContext) =
        declaration.declarations
            .filterIsInstance<IrClass>()
            .map { it.accept(data) }
            .merge()

    override fun visitClass(declaration: IrClass, data: ResolverContext) =
        buildList {
            addAll(declaration.declarations.filterIsInstance<IrClass>().map { it.accept(data) })
            if (data.shouldGenerateCode(declaration)) {
                addAll(declaration.functions.map { it.accept(data) })
            }
        }.merge()

    override fun visitFunction(declaration: IrFunction, data: ResolverContext) =
        if (declaration.accept(ShouldTransformCollector(data), Unit)) {
            val request = MappingResolver.of(declaration, ResolverContext(data, declaration)).resolve(declaration)
            mapOf(declaration.parentAsClass to request)
        } else {
            emptyMap()
        }
}