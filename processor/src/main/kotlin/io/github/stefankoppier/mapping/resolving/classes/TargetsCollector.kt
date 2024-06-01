package io.github.stefankoppier.mapping.resolving.classes

import io.github.stefankoppier.mapping.MappingPluginContext
import io.github.stefankoppier.mapping.resolving.BaseVisitor
import io.github.stefankoppier.mapping.resolving.ConstructorMappingTarget
import io.github.stefankoppier.mapping.resolving.MappingTarget
import io.github.stefankoppier.mapping.resolving.SingleResultMappingTarget
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.types.isPrimitiveType
import org.jetbrains.kotlin.ir.types.isString
import org.jetbrains.kotlin.ir.util.primaryConstructor
import org.jetbrains.kotlin.name.Name

class TargetsCollector(pluginContext: MappingPluginContext) : BaseVisitor<MappingTarget, Unit>(pluginContext) {

    override fun visitFunction(declaration: IrFunction, data: Unit): MappingTarget {
        return when  {
            declaration.returnType.isPrimitiveType() || declaration.returnType.isString() -> {
                SingleResultMappingTarget(declaration.returnType, declaration.body!!.accept(SingleResultTargetCollector(pluginContext), Unit))
            }
            else -> {
                declaration.returnType.getClass()!!.primaryConstructor!!.accept(this, Unit)
            }
        }
    }

    override fun visitConstructor(declaration: IrConstructor, data: Unit): MappingTarget {
        return ConstructorMappingTarget(declaration, declaration.valueParameters)
    }
}

class SingleResultTargetCollector(pluginContext: MappingPluginContext) : BaseVisitor<IrExpression, Unit>(pluginContext) {

    override fun visitBlockBody(body: IrBlockBody, data: Unit): IrExpression {
        require(body.statements.size == 1)
        return body.statements.first().accept(this, Unit)
    }

    override fun visitReturn(expression: IrReturn, data: Unit): IrExpression {
        return expression.value.accept(this, Unit)
    }

    override fun visitCall(expression: IrCall, data: Unit): IrExpression {
        return when (expression.symbol.owner.name) {
            Name.identifier("mapping") -> {
                expression.valueArguments.first()!!.accept(this, Unit)
            }
            Name.identifier("result") -> {
                expression.valueArguments.first()!!
            }
            else -> {
                super.visitCall(expression, data)
            }
        }
    }

    override fun visitFunctionExpression(expression: IrFunctionExpression, data: Unit): IrExpression {
        return expression.function.accept(this, Unit)
    }

    override fun visitFunction(declaration: IrFunction, data: Unit): IrExpression {
        return declaration.body!!.accept(this, Unit)
    }

    override fun visitTypeOperator(expression: IrTypeOperatorCall, data: Unit): IrExpression {
        return expression.argument.accept(this, Unit)
    }
}