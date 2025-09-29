package tech.mappie.ir.resolving

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.isNullable
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
    filter { mappie ->

        when {
            (source.isList() && target.isList()) || (source.isSet() && target.isSet()) -> {
                val source = (source as IrSimpleType).arguments.first().typeOrFail
                val target = (target as IrSimpleType).arguments.first().typeOrFail
                source.isMappableFrom(mappie.source) && mappie.target.isMappableFrom(target)
            }
            (source.isList() xor target.isList()) || (source.isSet() xor target.isSet()) -> {
                false
            }
            mappie.target.isNullable() -> {
                if (mappie.source.isNullable()) {
                    source.isMappableFrom(mappie.source) && mappie.target.isMappableFrom(target)
                } else {
                    source.makeNotNull().isMappableFrom(mappie.source) && mappie.target.isMappableFrom(target)
                }
            }
            else -> {
                if (mappie.source.isNullable()) {
                    source.isMappableFrom(mappie.source) && mappie.target.isMappableFrom(target)
                } else {
                    source.makeNotNull().isMappableFrom(mappie.source) && mappie.target.isMappableFrom(target)
                }
            }
        }
    }

// If target is nullable, then we can safely nullify source
// If target is not-nullable, then we cannot nullify source ->

// Input: InnerInput? -> InnerOutput
// Mappie: InnerInput -> InnerOutput