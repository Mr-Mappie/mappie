package tech.mappie.ir.resolving

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrLocalDelegatedProperty
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrBranch
import org.jetbrains.kotlin.ir.expressions.IrBreakContinue
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrCatch
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrConstantArray
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrContainerExpression
import org.jetbrains.kotlin.ir.expressions.IrDelegatingConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.expressions.IrGetEnumValue
import org.jetbrains.kotlin.ir.expressions.IrGetField
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.expressions.IrInstanceInitializerCall
import org.jetbrains.kotlin.ir.expressions.IrLocalDelegatedPropertyReference
import org.jetbrains.kotlin.ir.expressions.IrLoop
import org.jetbrains.kotlin.ir.expressions.IrReturn
import org.jetbrains.kotlin.ir.expressions.IrSetField
import org.jetbrains.kotlin.ir.expressions.IrThrow
import org.jetbrains.kotlin.ir.expressions.IrTry
import org.jetbrains.kotlin.ir.expressions.IrTypeOperatorCall
import org.jetbrains.kotlin.ir.expressions.IrWhen
import tech.mappie.ir.util.BaseVisitor
import tech.mappie.util.IDENTIFIER_MAPPING

object MappingStatementsFinder : BaseVisitor<IrFunctionExpression?, Unit>() {

    override fun visitClass(declaration: IrClass, data: Unit) =
        declaration.declarations.visitAll()

    override fun visitConstructor(declaration: IrConstructor, data: Unit) =
        declaration.body?.accept(data)
    override fun visitConstructorCall(expression: IrConstructorCall, data: Unit) =
        expression.arguments.visitAll()

    override fun visitInstanceInitializerCall(expression: IrInstanceInitializerCall, data: Unit) =
        null

    override fun visitProperty(declaration: IrProperty, data: Unit) =
        listOf(declaration.getter, declaration.setter).visitAll()

    override fun visitFunction(declaration: IrFunction, data: Unit) =
        declaration.body?.accept(data)

    override fun visitDelegatingConstructorCall(expression: IrDelegatingConstructorCall, data: Unit) =
        expression.arguments.visitAll()

    override fun visitLocalDelegatedProperty(declaration: IrLocalDelegatedProperty, data: Unit) =
        listOf(declaration.getter, declaration.setter).visitAll()

    override fun visitLocalDelegatedPropertyReference(expression: IrLocalDelegatedPropertyReference, data: Unit) =
        expression.arguments.visitAll()

    override fun visitBlockBody(body: IrBlockBody, data: Unit) =
        body.statements.visitAll()

    override fun visitWhen(expression: IrWhen, data: Unit) =
        expression.branches.visitAll()

    override fun visitBranch(branch: IrBranch, data: Unit) =
        listOf(branch.condition, branch.result).visitAll()

    override fun visitLoop(loop: IrLoop, data: Unit) =
        listOf(loop.condition, loop.body).visitAll()

    override fun visitContainerExpression(expression: IrContainerExpression, data: Unit) =
        expression.statements.visitAll()

    override fun visitReturn(expression: IrReturn, data: Unit) =
        expression.value.accept(data)

    override fun visitVariable(declaration: IrVariable, data: Unit) =
        declaration.initializer?.accept(data)

    override fun visitGetEnumValue(expression: IrGetEnumValue, data: Unit) =
        null

    override fun visitGetValue(expression: IrGetValue, data: Unit) =
        null

    override fun visitConstantArray(expression: IrConstantArray, data: Unit) =
        null

    override fun visitGetField(expression: IrGetField, data: Unit) =
        expression.receiver?.accept(data)

    override fun visitSetField(expression: IrSetField, data: Unit) =
        listOf(expression.value, expression.receiver).visitAll()

    override fun visitTypeOperator(expression: IrTypeOperatorCall, data: Unit) =
        expression.argument.accept(data)

    override fun visitTry(aTry: IrTry, data: Unit) =
        (listOf(aTry.tryResult, aTry.finallyExpression) + aTry.catches).visitAll()

    override fun visitCatch(aCatch: IrCatch, data: Unit) =
        aCatch.result.accept(data)

    override fun visitThrow(expression: IrThrow, data: Unit) =
        expression.value.accept(data)

    override fun visitBreakContinue(jump: IrBreakContinue, data: Unit)  =
        null

    override fun visitConst(expression: IrConst, data: Unit) =
        null

    override fun visitCall(expression: IrCall, data: Unit) =
        when (expression.symbol.owner.name) {
            IDENTIFIER_MAPPING -> expression.arguments.getOrNull(1) as? IrFunctionExpression
            else -> expression.arguments.visitAll()
        }

    override fun visitFunctionExpression(expression: IrFunctionExpression, data: Unit) =
        expression.function.accept(data)

    private fun List<IrElement?>.visitAll() =
        map { it?.accept(Unit) }.firstOrNull { it != null }
}