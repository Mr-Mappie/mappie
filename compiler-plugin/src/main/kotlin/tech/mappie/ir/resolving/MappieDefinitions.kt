package tech.mappie.ir.resolving

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.functions
import tech.mappie.ir.util.*

data class MappieDefinition(
    val clazz: IrClass,
    val source: IrType = (clazz.superTypes.first() as IrSimpleType).arguments[0].typeOrFail,
    val target: IrType = (clazz.superTypes.first() as IrSimpleType).arguments[1].typeOrFail,
) {
    fun referenceMapNullableListFunction() = clazz.functions.first { it.isMappieMapNullableListFunction() }
    fun referenceMapListFunction() = clazz.functions.first { it.isMappieMapListFunction() }
    fun referenceMapNullableSetFunction() = clazz.functions.first { it.isMappieMapNullableSetFunction() }
    fun referenceMapSetFunction() = clazz.functions.first { it.isMappieMapSetFunction() }
    fun referenceMapNullableFunction() = clazz.functions.first { it.isMappieMapNullableFunction() }
    fun referenceMapFunction() = clazz.functions.first { it.isMappieMapFunction() }
}

fun List<MappieDefinition>.matching(source: IrType, target: IrType) =
    filter {
        when {
            (source.isList() && target.isList()) || (source.isSet() && target.isSet()) -> {
                val source = (source as IrSimpleType).arguments.first().typeOrFail
                val target = (target as IrSimpleType).arguments.first().typeOrFail
                it.source.isMappableFrom(source) && target.isMappableFrom(it.target.makeNullable())
            }
            (source.isList() xor target.isList()) || (source.isSet() xor target.isSet()) -> {
                false
            }
            else -> {
                it.source.isMappableFrom(source) && target.isMappableFrom(it.target.makeNullable())
            }
        }
    }
