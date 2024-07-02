package tech.mappie.resolving.classes

import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.IrValueSymbol
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.typeOrFail
import org.jetbrains.kotlin.ir.util.isFunction

sealed interface ObjectMappingSource {
    val type: IrType
}

data class ResolvedSource(
    val property: IrSimpleFunctionSymbol,
    val dispatchReceiver: IrExpression,
    val via: IrSimpleFunction? = null,
    val viaDispatchReceiver: IrExpression? = null,
) : ObjectMappingSource {
    override val type: IrType
        get() = via?.returnType ?: property.owner.returnType
}

data class PropertySource(
    val property: IrSimpleFunctionSymbol,
    val dispatchReceiver: IrExpression,
    val transformation: IrFunctionExpression? = null,
    val origin: IrExpression,
) : ObjectMappingSource {

    override val type: IrType
        get() = if (transformation == null) {
            property.owner.returnType
        } else if (transformation.type.isFunction()) {
            (transformation.type as IrSimpleType).arguments[1].typeOrFail
        } else {
            transformation.type
        }
}

data class ExpressionSource(
    val extensionReceiverSymbol: IrValueSymbol,
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
