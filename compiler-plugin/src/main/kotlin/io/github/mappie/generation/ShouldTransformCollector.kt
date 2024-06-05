package io.github.mappie.generation

import io.github.mappie.BaseVisitor
import io.github.mappie.resolving.IDENTIFIER_MAPPING
import io.github.mappie.util.isSubclassOfFqName
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrReturn

class ShouldTransformCollector : BaseVisitor<Boolean, Unit> {
    override fun visitClass(declaration: IrClass, data: Unit): Boolean {
        return declaration.isSubclassOfFqName("io.github.mappie.api.Mapper")
                && declaration.declarations.filterIsInstance<IrSimpleFunction>().any { it.accept(this, Unit) }
    }

    override fun visitSimpleFunction(declaration: IrSimpleFunction, data: Unit): Boolean {
        return declaration.body?.accept(this, Unit) ?: false
    }

    override fun visitBlockBody(body: IrBlockBody, data: Unit): Boolean {
        return body.statements.singleOrNull()?.accept(this, Unit) ?: false
    }

    override fun visitReturn(expression: IrReturn, data: Unit): Boolean {
        return expression.value.accept(this, Unit)
    }

    override fun visitCall(expression: IrCall, data: Unit): Boolean {
        return expression.symbol.owner.name == IDENTIFIER_MAPPING
    }

    override fun visitElement(element: IrElement, data: Unit): Boolean {
        return false
    }
}