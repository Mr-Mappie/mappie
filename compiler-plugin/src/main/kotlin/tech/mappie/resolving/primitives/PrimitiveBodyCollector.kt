package tech.mappie.resolving.primitives

import tech.mappie.resolving.IDENTIFIER_MAPPING
import tech.mappie.util.irGet
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.*

class PrimitiveBodyCollector(
    private val declaration: IrFunction,
) : tech.mappie.BaseVisitor<IrExpression, Unit>() {

    override fun visitBlockBody(body: IrBlockBody, data: Unit): IrExpression {
        return body.statements.single().accept(data)
    }

    override fun visitReturn(expression: IrReturn, data: Unit): IrExpression {
        return expression.value.accept(data)
    }

    override fun visitCall(expression: IrCall, data: Unit): IrExpression {
        return when (expression.symbol.owner.name) {
            IDENTIFIER_MAPPING -> {
                expression.valueArguments.first()?.accept(data)
                    ?: irGet(declaration.valueParameters.first())
            }
            else -> {
                super.visitCall(expression, data)
            }
        }
    }

    override fun visitFunctionExpression(expression: IrFunctionExpression, data: Unit): IrExpression {
        return expression.function.accept(data)
    }

    override fun visitFunction(declaration: IrFunction, data: Unit): IrExpression {
        return declaration.body!!.accept(data)
    }

    override fun visitTypeOperator(expression: IrTypeOperatorCall, data: Unit): IrExpression {
        return expression.argument.accept(data)
    }
}