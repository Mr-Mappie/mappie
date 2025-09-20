package tech.mappie.ir.resolving.enums

import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.expressions.*
import tech.mappie.ir.util.BaseVisitor
import tech.mappie.util.IDENTIFIER_FROM_ENUM_ENTRY
import tech.mappie.util.IDENTIFIER_THROWN_BY_ENUM_ENTRY

object EnumMappingStatementCollector
    : BaseVisitor<Pair<IrEnumEntry, EnumMappingTarget>?, Unit>() {

    override fun visitCall(expression: IrCall, data: Unit): Pair<IrEnumEntry, EnumMappingTarget>? {
        return when (expression.symbol.owner.name) {
            IDENTIFIER_FROM_ENUM_ENTRY -> {
                val target = expression.arguments[1]!!
                val source = (expression.arguments[2]!! as IrGetEnumValue).symbol.owner
                return source to ExplicitEnumMappingTarget(target)
            }
            IDENTIFIER_THROWN_BY_ENUM_ENTRY -> {
                val target = expression.arguments[1]!!
                val source = (expression.arguments[2]!! as IrGetEnumValue).symbol.owner
                return source to ThrowingEnumMappingTarget(target)
            }
            else -> {
                super.visitCall(expression, data)
            }
        }
    }

//    override fun visitTypeOperator(expression: IrTypeOperatorCall, data: Unit): Pair<IrEnumEntry, EnumMappingTarget> {
//        return expression.argument.accept(data)
//    }

//    override fun visitFunction(declaration: IrFunction, data: Unit): Pair<IrEnumEntry, EnumMappingTarget> {
//        return declaration.body!!.accept(data)
//    }
//
    override fun visitReturn(expression: IrReturn, data: Unit) =
        null
//
//    override fun visitFunctionExpression(expression: IrFunctionExpression, data: Unit): Pair<IrEnumEntry, EnumMappingTarget> {
//        return expression.function.accept(data)
//    }

//    override fun visitBlockBody(body: IrBlockBody, data: Unit): Pair<Name, EnumMappingTarget> {
//        return body.statements.fold(data) { acc, current ->
//            acc.let { current.accept(it) }
//        }
//    }
}