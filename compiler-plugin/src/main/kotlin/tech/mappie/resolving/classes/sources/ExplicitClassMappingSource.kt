package tech.mappie.resolving.classes.sources

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrPropertyReference
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.makeNotNull
import org.jetbrains.kotlin.ir.types.typeOrFail

sealed interface ExplicitClassMappingSource : ClassMappingSource {
    val origin: IrElement
}

data class ExplicitPropertyMappingSource(
    val reference: IrPropertyReference,
    override val transformation : PropertyMappingTransformation?,
    val forceNonNull: Boolean,
) : ExplicitClassMappingSource, TransformableClassMappingSource {
    val getterType =  reference.getter!!.owner.returnType
    override val type = type(getterType.let { if (forceNonNull) it.makeNotNull() else it }, transformation)
    override val origin = reference
}

data class ValueMappingSource(val expression: IrExpression) : ExplicitClassMappingSource {
    override val type = expression.type
    override val origin = expression
}

data class ExpressionMappingSource(val expression: IrExpression) : ExplicitClassMappingSource {
    override val type = (expression.type as IrSimpleType).arguments[1].typeOrFail
    override val origin = expression
}