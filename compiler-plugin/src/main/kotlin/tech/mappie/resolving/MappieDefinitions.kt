package tech.mappie.resolving

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.functions
import tech.mappie.util.*

class MappieDefinitions(definition: List<MappieDefinition>) : List<MappieDefinition> by definition {

    fun select(source: IrType, target: IrType): List<MappieDefinition> =
        singleOrNull { it.fromType == source && it.toType == target }?.let { listOf(it) }
            ?: filter { it.fits(source, target) }
}

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
