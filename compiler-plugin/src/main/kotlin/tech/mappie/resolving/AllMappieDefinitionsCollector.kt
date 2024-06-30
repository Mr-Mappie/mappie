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
import tech.mappie.util.isAssignableFrom
import tech.mappie.util.isList
import tech.mappie.util.isSet
import tech.mappie.util.isStrictSubclassOf

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

class AllMappieDefinitionsCollector : BaseVisitor<List<MappieDefinition>, MutableList<MappieDefinition>>() {

    override fun visitModuleFragment(declaration: IrModuleFragment, data: MutableList<MappieDefinition>): List<MappieDefinition> {
        return declaration.files.flatMap { it.accept(data) }
    }

    override fun visitFile(declaration: IrFile, data: MutableList<MappieDefinition>): List<MappieDefinition> {
        return declaration.declarations.flatMap { it.accept(data) }
    }

    override fun visitClass(declaration: IrClass, data: MutableList<MappieDefinition>): List<MappieDefinition> {
        return data.apply {
            if (declaration.isStrictSubclassOf(Mappie::class)) {
                (declaration.superTypes.single() as? IrSimpleType)?.let {
                    add(
                        MappieDefinition(
                            fromType = it.arguments[0].typeOrFail,
                            toType = it.arguments[1].typeOrFail,
                            clazz = declaration,
                        )
                    )
                }
            }
            declaration.declarations.filterIsInstance<IrClass>().forEach { it.accept(data) }
        }
    }

    override fun visitElement(element: IrElement, data: MutableList<MappieDefinition>): List<MappieDefinition> {
        return data
    }
}