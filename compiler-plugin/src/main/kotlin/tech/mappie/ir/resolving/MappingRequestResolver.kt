package tech.mappie.ir.resolving

import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.util.*
import tech.mappie.ir.util.IrBaseVisitor
import tech.mappie.api.*
import tech.mappie.ir.util.isSubclassOf
import tech.mappie.util.merge

class MappingRequestResolver : IrBaseVisitor<Map<IrClass, List<MappingRequest>>, ResolverContext>() {

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
            if (declaration.isSubclassOf(Mappie::class)) {
                addAll(declaration.functions.map { it.accept(data) })
            }
        }.merge()

    override fun visitFunction(declaration: IrFunction, data: ResolverContext) =
        if (declaration.accept(ShouldTransformCollector(), Unit)) {
            mapOf(declaration.parentAsClass to MappingResolver.of(declaration, ResolverContext(data, declaration)).resolve(declaration.body))
        } else {
            emptyMap()
        }
}