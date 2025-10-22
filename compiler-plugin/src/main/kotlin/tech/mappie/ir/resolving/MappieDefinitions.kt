package tech.mappie.ir.resolving

import org.jetbrains.kotlin.backend.jvm.ir.upperBound
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.typeOrFail
import org.jetbrains.kotlin.ir.util.functions
import tech.mappie.ir.util.*

data class MappieDefinition(
    val clazz: IrClass,
    val source: IrType = (clazz.superTypes.first() as IrSimpleType).arguments[0].typeOrFail,
    val target: IrType = (clazz.superTypes.first() as IrSimpleType).arguments[1].typeOrFail,
) {
    fun referenceMapFunction() = clazz.functions.first { it.isMappieMapFunction() }
    fun referenceMapNullableFunction() = clazz.functions.first { it.isMappieMapNullableFunction() }
}

fun List<MappieDefinition>.matching(source: IrType, target: IrType) =
    filter { mappie ->
        source.upperBound.isSubtypeOf(mappie.source.upperBound) && mappie.target.upperBound.isSubtypeOf(target.upperBound)
    }