package tech.mappie.resolving

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.functions
import tech.mappie.BaseVisitor
import tech.mappie.api.Mappie
import tech.mappie.util.*

data class MappieDefinition(
    val clazz: IrClass,
    val fromType: IrType = (clazz.superTypes.first() as IrSimpleType).arguments[0].typeOrFail,
    val toType: IrType = (clazz.superTypes.first() as IrSimpleType).arguments[1].typeOrFail,
) {

    fun fits(sourceType: IrType, targetType: IrType): Boolean =
        sourceType.isNullAssignable(targetType) &&
                (fitsSimpleType(sourceType, targetType) || fitsList(sourceType, targetType) || fitsSet(sourceType, targetType))

    fun function(sourceType: IrType, targetType: IrType): IrFunction =
        clazz.functions.let { functions ->
            when {
                sourceType.isList() && targetType.isList() -> functions.first { it.isMappieMapListFunction() }
                sourceType.isSet() && targetType.isSet() -> functions.first { it.isMappieMapSetFunction() }
                sourceType.isNullable() && targetType.isNullable() -> functions.first { it.isMappieMapNullableFunction() }
                else -> functions.first { it.isMappieMapFunction() }
            }
        }

    private fun IrType.isNullAssignable(target: IrType) =
        !(isNullable() && !target.isNullable())

    private fun fitsSimpleType(sourceType: IrType, targetType: IrType) =
        fromType.makeNullable().isAssignableFrom(sourceType) && targetType.makeNullable().isAssignableFrom(toType)

    private fun fitsList(sourceType: IrType, targetType: IrType) =
        (sourceType.isList() && fromType.isAssignableFrom((sourceType as IrSimpleType).arguments.first().typeOrFail))
                &&
        (targetType.isList() && ((targetType as IrSimpleType).arguments.first().typeOrFail).isAssignableFrom(toType))

    private fun fitsSet(sourceType: IrType, targetType: IrType) =
        (sourceType.isSet() && fromType.isAssignableFrom((sourceType as IrSimpleType).arguments.first().typeOrFail))
                &&
        (targetType.isSet() && ((targetType as IrSimpleType).arguments.first().typeOrFail).isAssignableFrom(toType))
}

class AllMappieDefinitionsCollector : BaseVisitor<List<MappieDefinition>, Unit>(null) {

    override fun visitModuleFragment(declaration: IrModuleFragment, data: Unit) =
        declaration.files.flatMap { it.accept(data) }

    override fun visitFile(declaration: IrFile, data: Unit): List<MappieDefinition> {
        file = declaration.fileEntry
        return declaration.declarations.flatMap { it.accept(data) }
    }

    override fun visitClass(declaration: IrClass, data: Unit) =
        buildList {
            if (declaration.isStrictSubclassOf(Mappie::class)) {
                (declaration.superTypes.single() as? IrSimpleType)?.let {
                    add(MappieDefinition(declaration))
                }
            }
            addAll(declaration.declarations.filterIsInstance<IrClass>().flatMap { it.accept(data) })
        }

    override fun visitElement(element: IrElement, data: Unit) =
        emptyList<MappieDefinition>()
}