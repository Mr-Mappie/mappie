package tech.mappie.ir.util

import org.jetbrains.kotlin.ir.declarations.IrTypeParametersContainer
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.util.isSubtypeOf

import org.jetbrains.kotlin.name.StandardClassIds.Annotations.FlexibleNullability
import tech.mappie.exceptions.MappiePanicException.Companion.panic
import tech.mappie.ir.MappieIrRegistrar.Companion.context

// TODO: other isNothing is the wrong way around.
fun IrType.isSubtypeOf(other: IrType) =
    other.isNothing() || isSubtypeOf(other, IrTypeSystemContextImpl(context.irBuiltIns))

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