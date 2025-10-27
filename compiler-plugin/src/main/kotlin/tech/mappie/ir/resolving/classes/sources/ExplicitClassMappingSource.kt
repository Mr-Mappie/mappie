package tech.mappie.ir.resolving.classes.sources

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrPropertyReference
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.makeNotNull
import org.jetbrains.kotlin.ir.types.typeOrFail
import org.jetbrains.kotlin.ir.util.dumpKotlinLike

sealed interface ExplicitClassMappingSource : ClassMappingSource {
    val origin: IrElement
}

data class ExplicitPropertyMappingSource(
    val reference: IrPropertyReference,
    override val transformation : PropertyMappingTransformation?,
    val forceNonNull: Boolean,
) : ExplicitClassMappingSource, TransformableClassMappingSource {
    val getterType = (reference.type as IrSimpleType).arguments.last().typeOrFail
    override val type = type(getterType.let { if (forceNonNull) it.makeNotNull() else it })
    override val origin = reference

    override fun toString() = "${reference.getter!!.owner.name}: ${getterType.dumpKotlinLike()} via $transformation"
}

data class ValueMappingSource(val expression: IrExpression) : ExplicitClassMappingSource {
    override val type = expression.type
    override val origin = expression

    override fun toString() = "${expression.dumpKotlinLike()}: ${type.dumpKotlinLike()}"
}

data class ExpressionMappingSource(val expression: IrExpression) : ExplicitClassMappingSource {
    override val type = (expression.type as IrSimpleType).arguments[1].typeOrFail
    override val origin = expression

    override fun toString() = "${expression.dumpKotlinLike()}: ${type.dumpKotlinLike()}"
}