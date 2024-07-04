package tech.mappie.resolving.classes

import tech.mappie.BaseVisitor
import tech.mappie.MappieIrRegistrar.Companion.context
import tech.mappie.api.ObjectMappie
import tech.mappie.resolving.*
import tech.mappie.util.getterName
import tech.mappie.util.irGet
import tech.mappie.util.location
import tech.mappie.util.logError
import org.jetbrains.kotlin.ir.IrFileEntry
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.createExpressionBody
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrFunctionExpressionImpl
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.Name

class ObjectMappingBodyCollector(file: IrFileEntry)
    : BaseVisitor<ObjectMappingsConstructor, ObjectMappingsConstructor>(file) {

    override fun visitBlockBody(body: IrBlockBody, data: ObjectMappingsConstructor): ObjectMappingsConstructor {
        return body.statements.single().accept(data)
    }

    override fun visitReturn(expression: IrReturn, data: ObjectMappingsConstructor): ObjectMappingsConstructor {
        return expression.value.accept(data)
    }

    override fun visitCall(expression: IrCall, data: ObjectMappingsConstructor): ObjectMappingsConstructor {
        return when (expression.symbol.owner.name) {
            IDENTIFIER_MAPPING -> {
                expression.valueArguments.first()?.accept(data) ?: data
            }
            else -> {
                data
            }
        }
    }

    override fun visitFunctionExpression(expression: IrFunctionExpression, data: ObjectMappingsConstructor): ObjectMappingsConstructor {
        return expression.function.body?.statements?.fold(data) { acc, current ->
            acc.let { current.accept(ObjectBodyStatementCollector(file!!), Unit)?.let { acc.explicit(it) } ?: it }
        } ?: data
    }
}

private class ObjectBodyStatementCollector(
    file: IrFileEntry,
) : BaseVisitor<Pair<Name, ObjectMappingSource>?, Unit>(file) {

    override fun visitCall(expression: IrCall, data: Unit): Pair<Name, ObjectMappingSource>? {
        return when (expression.symbol.owner.name) {
            IDENTIFIER_FROM_PROPERTY -> {
                val target = expression.extensionReceiver!!.accept(TargetValueCollector(file!!), data)
                val source = expression.valueArguments.first()!!.accept(SourceValueCollector(), Unit)

                target to source
            }
            IDENTIFIER_FROM_VALUE -> {
                val target = expression.extensionReceiver!!.accept(TargetValueCollector(file!!), data)
                val source = expression.valueArguments.first()!!

                target to ValueSource(source, expression)
            }
            IDENTIFIER_FROM_EXPRESSION -> {
                val target = expression.extensionReceiver!!.accept(TargetValueCollector(file!!), data)
                val source = expression.valueArguments.first() as IrFunctionExpression

                target to ExpressionSource(
                    source,
                    expression,
                )
            }
            IDENTIFIER_TRANFORM -> {
                val mapping = expression.dispatchReceiver!!.accept(data)!!
                val transformation = expression.valueArguments.first()!! as IrFunctionExpression
                mapping.first to (mapping.second as PropertySource).copy(transformation = transformation)
            }
            IDENTIFIER_VIA -> {
                val mapping = expression.dispatchReceiver!!.accept(data)!!
                val transformation = expression.valueArguments.first()!!.accept(MapperReferenceCollector(), Unit)
                mapping.first to (mapping.second as PropertySource).copy(
                    transformation = transformation
                )
            }
            else -> {
                logError("Unexpected method call", file?.let { location(it, expression) })
                return null
            }
        }
    }

    override fun visitTypeOperator(expression: IrTypeOperatorCall, data: Unit): Pair<Name, ObjectMappingSource>? {
        return when (expression.operator.name) {
            "IMPLICIT_COERCION_TO_UNIT" -> expression.argument.accept(data)
            else -> error(expression.operator.name)
        }
    }

    override fun visitReturn(expression: IrReturn, data: Unit): Pair<Name, ObjectMappingSource>? {
        return expression.value.accept(data)
    }

    override fun visitGetObjectValue(expression: IrGetObjectValue, data: Unit): Pair<Name, ObjectMappingSource>? {
        return null
    }
}

private class MapperReferenceCollector : BaseVisitor<IrFunctionExpression, Unit>() {

    override fun visitGetObjectValue(expression: IrGetObjectValue, data: Unit): IrFunctionExpression {
        return context.referenceClass(expression.symbol.owner.classId!!)!!
            .functions
            .filter { it.owner.name == IDENTIFIER_MAP }
            .first()
            .wrap(expression)
    }

    override fun visitConstructorCall(expression: IrConstructorCall, data: Unit): IrFunctionExpression {
        return expression.type.getClass()!!.symbol.functions
            .filter { it.owner.name == IDENTIFIER_MAP }
            .first()
            .wrap(expression)
    }

    override fun visitCall(expression: IrCall, data: Unit): IrFunctionExpression {
        require(expression.origin == IrStatementOrigin.GET_PROPERTY)

        return when (val name = expression.symbol.owner.name) {
            getterName(ObjectMappie<*, *>::forList.name) -> {
                val mapper = expression.symbol.owner.parent as IrClass

                val function = mapper.functions
                    .filter { it.name == IDENTIFIER_MAP_LIST }
                    .first()

                function.symbol.wrap(expression.dispatchReceiver!!)
            }
            getterName(ObjectMappie<*, *>::forSet.name) -> {
                val mapper = expression.symbol.owner.parent as IrClass

                val function = mapper.functions
                    .filter { it.name == IDENTIFIER_MAP_SET }
                    .first()

                function.symbol.wrap(expression.dispatchReceiver!!)
            }
            else -> {
                logError("Unexpected call of ${name.asString()}, expected forList or forSet", file?.let { location(it, expression) })
                error("Expected forList or forSet")
            }
        }
    }

    private fun IrSimpleFunctionSymbol.wrap(receiver: IrExpression): IrFunctionExpression =
        IrFunctionExpressionImpl(
            SYNTHETIC_OFFSET,
            SYNTHETIC_OFFSET,
            owner.returnType,
            context.irFactory.buildFun {
                name = Name.identifier("stub_for_inlining")
                returnType = owner.returnType
            }.apply {
                parent = owner.parent
                val itParameter = addValueParameter {
                    name = IDENTIFIER_IT
                    type = owner.valueParameters.single().type
                    index = 0
                }
                body = context.irFactory.createExpressionBody(IrCallImpl(
                    SYNTHETIC_OFFSET,
                    SYNTHETIC_OFFSET,
                    owner.returnType,
                    owner.symbol,
                    0,
                    1,
                ).apply {
                    dispatchReceiver = receiver
                    putValueArgument(0, irGet(itParameter))
                })
            },
            IrStatementOrigin.LAMBDA
        )
}

private class SourceValueCollector : BaseVisitor<ObjectMappingSource, Unit>() {

    override fun visitPropertyReference(expression: IrPropertyReference, data: Unit): ObjectMappingSource {
        return PropertySource(
            property = expression,
            transformation = null,
            origin = expression,
        )
    }

    override fun visitConst(expression: IrConst<*>, data: Unit): ObjectMappingSource {
        return ValueSource(expression, expression)
    }
}

private class TargetValueCollector(file: IrFileEntry) : BaseVisitor<Name, Unit>(file) {

    override fun visitPropertyReference(expression: IrPropertyReference, data: Unit): Name {
        return expression.symbol.owner.name
    }

    override fun visitCall(expression: IrCall, data: Unit): Name {
        return when (expression.symbol.owner.name) {
            IDENTIFIER_PARAMETER -> {
                val value = expression.valueArguments.first()!!
                return if (value.isConstantLike && value is IrConst<*>) {
                    Name.identifier(value.value as String)
                } else {
                    logError("Parameter name must be a constant", location(file!!, expression))
                    throw AssertionError()
                }
            }
            else -> {
                super.visitCall(expression, data)
            }
        }
    }
}