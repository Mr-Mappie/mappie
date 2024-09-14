package tech.mappie.util

import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.classId
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.name.StandardClassIds.Annotations.FlexibleNullability

fun IrType.isMappableFrom(other: IrType): Boolean = when {
    (isList() && other.isList()) || (isSet() && other.isSet()) ->
        (this as IrSimpleType).arguments.first().typeOrFail.isMappableFrom((other as IrSimpleType).arguments.first().typeOrFail)
    (isList() xor other.isList()) || (isSet() xor other.isSet()) ->
        false
    else ->
        isSubtypeOfClass(other.classOrFail)
}

fun IrType.isList() =
    classOrNull?.owner?.fqNameWhenAvailable?.asString() == List::class.qualifiedName

fun IrType.isSet() =
    classOrNull?.owner?.fqNameWhenAvailable?.asString() == Set::class.qualifiedName

fun IrType.hasFlexibleNullabilityAnnotation(): Boolean =
    annotations.any { it.symbol.owner.parentAsClass.classId == FlexibleNullability }
