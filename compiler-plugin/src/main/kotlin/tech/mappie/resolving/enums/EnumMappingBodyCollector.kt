package tech.mappie.resolving.enums

import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.*
import tech.mappie.BaseVisitor
import tech.mappie.util.IDENTIFIER_FROM_ENUM_ENTRY
import tech.mappie.util.IDENTIFIER_MAPPING
import tech.mappie.util.IDENTIFIER_THROWN_BY_ENUM_ENTRY

class EnumMappingBodyCollector : BaseVisitor<EnumMappingRequestBuilder, EnumMappingRequestBuilder>() {

    override fun visitCall(expression: IrCall, data: EnumMappingRequestBuilder): EnumMappingRequestBuilder {
        return when (expression.symbol.owner.name) {
            IDENTIFIER_MAPPING -> {
                expression.valueArguments.first()?.accept(data) ?: data
            }
            IDENTIFIER_FROM_ENUM_ENTRY -> {
                val target = expression.extensionReceiver!!
                val source = (expression.valueArguments.first()!! as IrGetEnumValue).symbol.owner
                data.explicit(source to ExplicitEnumMappingTarget(target, expression))
            }
            IDENTIFIER_THROWN_BY_ENUM_ENTRY -> {
                val target = expression.extensionReceiver!!
                val source = (expression.valueArguments.first()!! as IrGetEnumValue).symbol.owner
                data.explicit(source to ThrowingEnumMappingTarget(target, expression))
            }
            else -> {
                super.visitCall(expression, data)
            }
        }
    }

    override fun visitTypeOperator(expression: IrTypeOperatorCall, data: EnumMappingRequestBuilder): EnumMappingRequestBuilder {
        return expression.argument.accept(data)
    }

    override fun visitFunction(declaration: IrFunction, data: EnumMappingRequestBuilder): EnumMappingRequestBuilder {
        return declaration.body!!.accept(data)
    }

    override fun visitReturn(expression: IrReturn, data: EnumMappingRequestBuilder): EnumMappingRequestBuilder {
        return expression.value.accept(data)
    }

    override fun visitFunctionExpression(expression: IrFunctionExpression, data: EnumMappingRequestBuilder): EnumMappingRequestBuilder {
        return expression.function.accept(data)
    }

    override fun visitBlockBody(body: IrBlockBody, data: EnumMappingRequestBuilder): EnumMappingRequestBuilder {
        return body.statements.fold(data) { acc, current ->
            acc.let { current.accept(it) }
        }
    }
}