package io.github.mappie.resolving.primitives

import io.github.mappie.BaseVisitor
import io.github.mappie.resolving.IDENTIFIER_MAPPING
import io.github.mappie.resolving.IDENTIFIER_RESULT
import io.github.mappie.resolving.SingleValueMapping
import io.github.mappie.util.irGet
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.types.isPrimitiveType
import org.jetbrains.kotlin.ir.types.isString

class PrimitiveResolver(private val declaration: IrFunction) {

    private val targetType = declaration.returnType

    init {
        check(targetType.isPrimitiveType() || targetType.isString())
    }

    fun resolve(): SingleValueMapping {
        return SingleValueMapping(targetType, declaration.body!!.accept(SingleResultTargetCollector(declaration), Unit))
    }
}

private class SingleResultTargetCollector(
    private val declaration: IrFunction,
) : BaseVisitor<IrExpression, Unit>() {

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
            IDENTIFIER_RESULT -> {
                expression.valueArguments.first()!!
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