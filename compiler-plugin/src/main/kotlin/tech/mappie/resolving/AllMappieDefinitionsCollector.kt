package tech.mappie.resolving

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.typeOrFail
import tech.mappie.BaseVisitor
import tech.mappie.api.Mappie
import tech.mappie.util.*

data class MappieDefinition(
    val fromType: IrType,
    val toType: IrType,
    val clazz: IrClass,
) {
    fun fits(sourceType: IrType, targetType: IrType): Boolean =
        (fromType.isAssignableFrom(sourceType) && targetType.isAssignableFrom(toType))
                || fitsList(sourceType, targetType)
                || fitsSet(sourceType, targetType)

    private fun fitsList(sourceType: IrType, targetType: IrType) =
        (sourceType.isList() && fromType.isAssignableFrom((sourceType as IrSimpleType).arguments.first().typeOrFail))
                &&
        (targetType.isList() && ((targetType as IrSimpleType).arguments.first().typeOrFail).isAssignableFrom(toType))

    private fun fitsSet(sourceType: IrType, targetType: IrType) =
        (sourceType.isSet() && fromType.isAssignableFrom((sourceType as IrSimpleType).arguments.first().typeOrFail))
                &&
        (targetType.isSet() && ((targetType as IrSimpleType).arguments.first().typeOrFail).isAssignableFrom(toType))
}

class AllMappieDefinitionsCollector : BaseVisitor<List<MappieDefinition>, Unit>() {

    override fun visitModuleFragment(declaration: IrModuleFragment, data: Unit) =
        declaration.files.flatMap { it.accept(data) }

    override fun visitFile(declaration: IrFile, data: Unit) =
        declaration.declarations.flatMap { it.accept(data) }

    override fun visitClass(declaration: IrClass, data: Unit) =
        buildList {
            if (declaration.isStrictSubclassOf(Mappie::class)) {
                (declaration.superTypes.single() as? IrSimpleType)?.let {
                    add(MappieDefinition(it.arguments[0].typeOrFail, it.arguments[1].typeOrFail, declaration))
                }
            }
            addAll(declaration.declarations.filterIsInstance<IrClass>().flatMap { it.accept(data) })
        }

    override fun visitElement(element: IrElement, data: Unit) =
        emptyList<MappieDefinition>()
}