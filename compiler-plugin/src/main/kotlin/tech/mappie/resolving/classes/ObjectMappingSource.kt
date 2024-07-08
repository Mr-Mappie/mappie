package tech.mappie.resolving.classes

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.expressions.IrPropertyReference
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.typeOrFail
import org.jetbrains.kotlin.ir.util.isFunction

sealed interface ObjectMappingSource {
    val type: IrType
}

data class ResolvedSource(
    val property: MappieGetter,
    val via: Pair<IrClass, IrSimpleFunction>? = null,
) : ObjectMappingSource {
    override val type: IrType
        get() = via?.second?.returnType ?: property.type
}

data class PropertySource(
    val property: IrPropertyReference,
    val transformation: MappieTransformation? = null,
    val origin: IrExpression,
) : ObjectMappingSource {

    val getter = property.getter!!

    override val type: IrType
        get() = if (transformation == null) {
            getter.owner.returnType
        } else if (transformation.type.isFunction()) {
            (transformation.type as IrSimpleType).arguments[1].typeOrFail
        } else {
            transformation.type
        }
}

data class ExpressionSource(
    val expression: IrFunctionExpression,
    val origin: IrExpression,
) : ObjectMappingSource {
    override val type = expression.function.returnType
}

data class ValueSource(
    val value: IrExpression,
    val origin: IrExpression?,
) : ObjectMappingSource {
    override val type = value.type
}