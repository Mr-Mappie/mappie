package tech.mappie.ir.util

import org.jetbrains.kotlin.ir.declarations.IrTypeParametersContainer
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.util.isNullable
import org.jetbrains.kotlin.ir.util.isSubtypeOf

import org.jetbrains.kotlin.name.StandardClassIds.Annotations.FlexibleNullability
import tech.mappie.exceptions.MappiePanicException.Companion.panic
import tech.mappie.ir.MappieIrRegistrar.Companion.context

fun IrType.isMappableFrom(other: IrType): Boolean = when {
    (isList() && other.isList()) || (isSet() && other.isSet()) ->
        (this as IrSimpleType).arguments.first().typeOrFail.isMappableFrom((other as IrSimpleType).arguments.first().typeOrFail)
    (isList() xor other.isList()) || (isSet() xor other.isSet()) ->
        false
    else ->
        isSubtypeOf(other, IrTypeSystemContextImpl(context.irBuiltIns))
}

fun IrType.mappieType() = when {
    isList() || isSet() -> (this as IrSimpleType).arguments.first().typeOrFail
    isNullable() -> this.makeNotNull()
    else -> this
}

fun IrType.isList() =
    classOrNull?.owner?.fqNameWhenAvailable?.asString() == List::class.qualifiedName

fun IrType.isSet() =
    classOrNull?.owner?.fqNameWhenAvailable?.asString() == Set::class.qualifiedName

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