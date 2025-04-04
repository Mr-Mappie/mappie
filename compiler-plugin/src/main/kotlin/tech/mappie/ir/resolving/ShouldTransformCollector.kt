package tech.mappie.ir.resolving

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrReturn
import tech.mappie.MappieContext
import tech.mappie.ir.util.BaseVisitor
import tech.mappie.util.IDENTIFIER_MAPPING
import tech.mappie.ir.util.isMappieMapFunction
import tech.mappie.shouldGenerateCode

class ShouldTransformCollector(private val context: MappieContext) : BaseVisitor<Boolean, Unit>() {
    override fun visitClass(declaration: IrClass, data: Unit): Boolean {
        return context.shouldGenerateCode(declaration)
    }

    override fun visitSimpleFunction(declaration: IrSimpleFunction, data: Unit): Boolean {
        return declaration.body?.accept(data) ?: declaration.isMappieMapFunction()
    }

    override fun visitBlockBody(body: IrBlockBody, data: Unit): Boolean {
        return body.statements.singleOrNull()?.accept(data) ?: false
    }

    override fun visitReturn(expression: IrReturn, data: Unit): Boolean {
        return expression.value.accept(data)
    }

    override fun visitCall(expression: IrCall, data: Unit): Boolean {
        return expression.symbol.owner.name in arrayOf(IDENTIFIER_MAPPING)
    }

    override fun visitElement(element: IrElement, data: Unit): Boolean {
        return false
    }
}