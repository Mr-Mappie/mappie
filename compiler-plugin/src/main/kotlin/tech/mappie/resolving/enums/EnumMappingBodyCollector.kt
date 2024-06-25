package tech.mappie.resolving.enums

import tech.mappie.resolving.IDENTIFIER_MAPPED_FROM_ENUM_ENTRY
import tech.mappie.resolving.IDENTIFIER_MAPPING
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.*
import tech.mappie.BaseVisitor

data class ExplicitEnumMapping(
    val target: IrEnumEntry,
    val origin: IrExpression,
)

class EnumMappingBodyCollector : BaseVisitor<EnumMappingsConstructor, EnumMappingsConstructor>() {

    override fun visitCall(expression: IrCall, data: EnumMappingsConstructor): EnumMappingsConstructor {
        return when (expression.symbol.owner.name) {
            IDENTIFIER_MAPPING -> {
                expression.valueArguments.first()?.accept(data) ?: data
            }
            IDENTIFIER_MAPPED_FROM_ENUM_ENTRY -> {
                val target = (expression.extensionReceiver!! as IrGetEnumValue).symbol.owner
                val source = (expression.valueArguments.first()!! as IrGetEnumValue).symbol.owner
                data.explicit(source to ExplicitEnumMapping(target, expression))
            }
            else -> {
                super.visitCall(expression, data)
            }
        }
    }

    override fun visitTypeOperator(expression: IrTypeOperatorCall, data: EnumMappingsConstructor): EnumMappingsConstructor {
        return expression.argument.accept(data)
    }

    override fun visitFunction(declaration: IrFunction, data: EnumMappingsConstructor): EnumMappingsConstructor {
        return declaration.body!!.accept(data)
    }

    override fun visitReturn(expression: IrReturn, data: EnumMappingsConstructor): EnumMappingsConstructor {
        return expression.value.accept(data)
    }

    override fun visitFunctionExpression(expression: IrFunctionExpression, data: EnumMappingsConstructor): EnumMappingsConstructor {
        return expression.function.accept(data)
    }

    override fun visitBlockBody(body: IrBlockBody, data: EnumMappingsConstructor): EnumMappingsConstructor {
        return body.statements.fold(data) { acc, current ->
            acc.let { current.accept(it) }
        }
    }
}