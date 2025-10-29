package tech.mappie.ir.util

import org.jetbrains.kotlin.backend.jvm.ir.isWithFlexibleNullability
import org.jetbrains.kotlin.ir.declarations.IrTypeParametersContainer
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.util.isSubtypeOf

import org.jetbrains.kotlin.name.StandardClassIds.Annotations.FlexibleNullability
import tech.mappie.ir.MappieContext
import tech.mappie.exceptions.MappiePanicException.Companion.panic

// TODO: other isNothing is the wrong way around.
context(context: MappieContext)
fun IrType.isSubtypeOf(other: IrType): Boolean {
    val current = if (isWithFlexibleNullability()) {
        makeNotNull()
    } else {
        this
    }
    return other.isNothing() || current.isSubtypeOf(other, IrTypeSystemContextImpl(context.pluginContext.irBuiltIns))
}

fun IrType.hasFlexibleNullabilityAnnotation(): Boolean =
    annotations.any { it.symbol.owner.parentAsClass.classId == FlexibleNullability }

fun IrType.isPrimitive(): Boolean =
    isBoolean() || isByte() || isShort() || isInt() || isLong() || isChar() || isFloat() || isDouble()
            || isStringClassType() || isUByte() || isUShort() || isUInt() || isULong() || isNumber()

fun IrType.substituteTypeVariable(container: IrTypeParametersContainer, arguments: List<IrTypeArgument>) =
    if (isTypeParameter()) {
        val mapping = container.typeParameters.zip(arguments).toMap()
        mapping[classifierOrNull!!.owner]?.typeOrNull ?: panic("Could not resolve generic type", container)
    } else {
        this
    }