package tech.mappie.ir.resolving

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.visitors.IrVisitor
import tech.mappie.util.IDENTIFIER_MAPPING

object MappingStatementsFinder : IrVisitor<Unit, MutableList<IrFunctionExpression>>() {
    override fun visitElement(element: IrElement, data: MutableList<IrFunctionExpression>) {
        element.acceptChildren(this, data)
    }

    override fun visitCall(expression: IrCall, data: MutableList<IrFunctionExpression>) {
        when (expression.symbol.owner.name) {
            IDENTIFIER_MAPPING -> (expression.arguments.getOrNull(1) as? IrFunctionExpression)?.let { data.add(it) }
            else -> expression.acceptChildren(this, data)
        }
    }
}
