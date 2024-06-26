package tech.mappie.resolving.classes

import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.IrValueSymbol
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.typeOrFail
import org.jetbrains.kotlin.ir.util.isFunction

sealed interface ObjectMappingSource {
    fun resolveType(): IrType
}

data class PropertySource(
    val property: IrSimpleFunctionSymbol,
    val type: IrType,
    val dispatchReceiverSymbol: IrValueSymbol,
    val isResolvedAutomatically: Boolean,
    val transformation: IrFunctionExpression? = null,
    val origin: IrExpression? = null,
) : ObjectMappingSource {
    override fun resolveType(): IrType {
        return if (transformation == null) {
            type
        } else if (transformation.type.isFunction()) {
            (transformation.type as IrSimpleType).arguments[1].typeOrFail
        } else {
            transformation.type
        }
    }
}

data class ExpressionSource(
    val extensionReceiverSymbol: IrValueSymbol,
    val expression: IrFunctionExpression,
    val origin: IrExpression?,
) : ObjectMappingSource {
    override fun resolveType() = expression.function.returnType
}

data class ValueSource(
    val value: IrExpression,
) : ObjectMappingSource {
    override fun resolveType() = value.type
}
