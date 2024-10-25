package tech.mappie.resolving

import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.util.*
import tech.mappie.util.BaseVisitor
import tech.mappie.api.*
import tech.mappie.util.isMappieMapFunction
import tech.mappie.util.isSubclassOf
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
        buildList<Map<IrClass, List<MappingRequest>>> {
            addAll(declaration.declarations.filterIsInstance<IrClass>().map { it.accept(data) })
            if (declaration.isSubclassOf(Mappie::class)) {
                add(declaration.functions.firstOrNull { it.isMappieMapFunction() }!!.accept(data))
            }
        }.merge()

    override fun visitSimpleFunction(declaration: IrSimpleFunction, data: ResolverContext) =
        if (declaration.isMappieMapFunction()) {
            mapOf(declaration.parentAsClass to MappingResolver.of(declaration, ResolverContext(data, declaration)).resolve(declaration.body))
        } else {
            emptyMap()
        }
}