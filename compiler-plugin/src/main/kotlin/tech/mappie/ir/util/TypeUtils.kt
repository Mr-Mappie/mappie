package tech.mappie.ir.util

import org.jetbrains.kotlin.backend.jvm.ir.isWithFlexibleNullability
import org.jetbrains.kotlin.ir.declarations.IrTypeParametersContainer
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.util.isSubtypeOf

import org.jetbrains.kotlin.name.StandardClassIds.Annotations.FlexibleNullability
import tech.mappie.ir.MappieContext
import tech.mappie.exceptions.MappiePanicException.Companion.panic
import tech.mappie.ir.allMappieClasses

context(context: MappieContext)
fun IrType.isSubtypeOf(other: IrType): Boolean {
    val current = if (isWithFlexibleNullability()) {
        makeNotNull()
    } else {
        this
    }
    val other = if (other.isWithFlexibleNullability()) {
        other.makeNotNull()
    } else {
        other
    }
    return current.isSubtypeOf(other, IrTypeSystemContextImpl(context.pluginContext.irBuiltIns))
}

context(context: MappieContext)
fun IrType.mappieSourceAndTarget(): Pair<IrType, IrType> {
    val parent = superTypes()
        .single { !it.isInterface() }
        .substitute(classOrFail.owner.typeParameters, arguments.map { it.typeOrFail })

    return if (parent.classOrFail in allMappieClasses()) {
        parent.let {
            it.arguments.first().typeOrFail to it.arguments.last().typeOrFail
        }
    } else {
        parent.mappieSourceAndTarget()
    }
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

val IrType.arguments: List<IrTypeArgument>
    get() = when (this) {
        is IrSimpleType -> this.arguments
        else -> emptyList()
    }

/**
 * Erases type parameters by substituting them with concrete type arguments from the container type.
 * This is used for matching generic mappers and conversion methods.
 */
context(context: MappieContext)
fun IrType.erased(container: IrType): IrType {
    return if (arguments.any { it.typeOrNull?.isTypeParameter() ?: false }) {
        classOrFail.owner.typeWith(container.arguments.map { if (it is IrStarProjection) context.pluginContext.irBuiltIns.anyType.makeNullable() else it.typeOrFail })
    } else {
        this
    }
}