package tech.mappie.ir.resolving

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.visitors.IrVisitor
import tech.mappie.util.IDENTIFIER_MAPPING

fun findMappingStatements(element: IrElement?): List<IrCall> {
    return if (element != null) {
        val statements = mutableListOf<IrCall>()
        element.accept(MappingStatementsFinder, statements)
        statements
    } else {
        emptyList()
    }
}

private object MappingStatementsFinder : IrVisitor<Unit, MutableList<IrCall>>() {
    override fun visitElement(element: IrElement, data: MutableList<IrCall>) {
        element.acceptChildren(this, data)
    }

    override fun visitCall(expression: IrCall, data: MutableList<IrCall>) {
        when (expression.symbol.owner.name) {
            IDENTIFIER_MAPPING -> data.add(expression)
            else -> expression.acceptChildren(this, data)
        }
    }
}
