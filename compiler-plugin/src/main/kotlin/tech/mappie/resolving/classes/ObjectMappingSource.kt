package tech.mappie.resolving.classes

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.expressions.IrPropertyReference
import org.jetbrains.kotlin.ir.types.*
import tech.mappie.resolving.classes.sources.ClassMappingSource

//sealed interface ObjectMappingSource {
//    val type: IrType
//    val origin: IrElement
//}
//
//data class ResolvedSource(
//    val property: ClassMappingSource,
//    val transformation: List<MappieTransformation>,
//    val transformationType: IrType? = null,
//    override val origin: IrElement,
//) : ObjectMappingSource {
//    override val type: IrType get() = transformationType ?: property.type
//}
//
//data class PropertySource(
//    val property: IrPropertyReference,
//    val transformation: List<MappieTransformation> = emptyList(),
//    override val origin: IrElement,
//) : ObjectMappingSource {
//
//    val getter = property.getter!!
//
//    override val type: IrType get() =
//        if (transformation.isEmpty()) {
//            getter.owner.returnType
//        } else {
//            when (val transformation = transformation.first()) {
//                is MappieTransformOperator -> (transformation.type as IrSimpleType).arguments[1].typeOrFail
//                is MappieViaOperator -> if (getter.owner.returnType.isNullable()) transformation.type.makeNullable() else transformation.type
//                is MappieViaResolved -> if (getter.owner.returnType.isNullable()) transformation.type.makeNullable() else transformation.type
//                is MappieViaGeneratedClass -> if (getter.owner.returnType.isNullable()) transformation.type.makeNullable() else transformation.type
//            }
//        }
//}
//
//data class ExpressionSource(
//    val expression: IrFunctionExpression,
//    override val origin: IrElement,
//) : ObjectMappingSource {
//    override val type = expression.function.returnType
//}
//
//data class ValueSource(
//    val value: IrExpression,
//    override val origin: IrElement,
//) : ObjectMappingSource {
//    override val type = value.type
//}
//
//class DefaultArgumentSource(
//    override val type: IrType,
//    override val origin: IrElement,
//) : ObjectMappingSource