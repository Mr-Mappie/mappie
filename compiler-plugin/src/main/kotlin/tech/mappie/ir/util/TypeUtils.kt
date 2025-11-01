package tech.mappie.ir.util

import org.jetbrains.kotlin.backend.jvm.ir.isWithFlexibleNullability
import org.jetbrains.kotlin.backend.jvm.ir.upperBound
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrTypeParametersContainer
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.util.isSubtypeOf

import org.jetbrains.kotlin.name.StandardClassIds.Annotations.FlexibleNullability
import tech.mappie.ir.MappieContext
import tech.mappie.exceptions.MappiePanicException.Companion.panic
import tech.mappie.ir.allMappieClasses

// TODO: other isNothing is the wrong way around.
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
    return other.isNothing() || current.isSubtypeOf(other, IrTypeSystemContextImpl(context.pluginContext.irBuiltIns))
}

context(context: MappieContext)
fun IrClass.mappieSuperClassTypes(): Pair<IrType, IrType> {
    val type = allSuperTypes().single { it.classOrNull in allMappieClasses() } as IrSimpleType
    val source = type.arguments.first().typeOrFail.eraseFrom(this)
    val target = type.arguments.last().typeOrFail.eraseFrom(this)
    return source to target
}

fun IrType.eraseFrom(definition: IrClass): IrType {
    return if (isTypeParameter()) {
        this.upperBound // TODO: substitute type argument from super classes. (question kotlinlang slack)
    } else {
        this
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