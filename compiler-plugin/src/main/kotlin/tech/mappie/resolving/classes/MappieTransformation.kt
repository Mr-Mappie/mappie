package tech.mappie.resolving.classes

import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.types.IrType

sealed interface MappieTransformation {
    val type: IrType
}

data class MappieTransformTransformation(
    val function: IrFunctionExpression,
) : MappieTransformation {
    override val type: IrType = function.type
}

data class MappieViaTransformation(
    val function: IrSimpleFunction,
    val dispatchReceiver: IrExpression,
) : MappieTransformation {
    override val type = function.returnType
}