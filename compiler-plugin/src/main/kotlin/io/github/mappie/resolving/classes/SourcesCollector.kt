package io.github.mappie.resolving.classes

import io.github.mappie.BaseVisitor
import io.github.mappie.MappieIrRegistrar.Companion.context
import io.github.mappie.api.DataClassMapper
import io.github.mappie.resolving.*
import io.github.mappie.util.getterName
import io.github.mappie.util.irGet
import io.github.mappie.util.location
import io.github.mappie.util.logError
import org.jetbrains.kotlin.ir.IrFileEntry
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.declarations.createExpressionBody
import org.jetbrains.kotlin.ir.declarations.impl.IrClassImpl
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrFunctionExpressionImpl
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.IrValueSymbol
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.Name

class ObjectBodyCollector(
    file: IrFileEntry,
    private val dispatchReceiverSymbol: IrValueSymbol,
) : BaseVisitor<List<Pair<Name, ObjectMappingSource>>, Unit>(file) {

    override fun visitBlockBody(body: IrBlockBody, data: Unit): List<Pair<Name, ObjectMappingSource>> {
        return body.statements.single().accept(data)
    }

    override fun visitReturn(expression: IrReturn, data: Unit): List<Pair<Name, ObjectMappingSource>> {
        return expression.value.accept(data)
    }

    override fun visitCall(expression: IrCall, data: Unit): List<Pair<Name, ObjectMappingSource>> {
        return when (expression.symbol.owner.name) {
            IDENTIFIER_MAPPING -> {
                expression.valueArguments.first()?.accept(data) ?: emptyList()
            }
            else -> {
                emptyList()
            }
        }
    }

    override fun visitFunctionExpression(expression: IrFunctionExpression, data: Unit): List<Pair<Name, ObjectMappingSource>> {
        return expression.function.body!!.statements.mapNotNull {
            it.accept(ObjectBodyStatementCollector(file, dispatchReceiverSymbol), Unit)
        }
    }
}

private class ObjectBodyStatementCollector(
    file: IrFileEntry?,
    private val dispatchReceiverSymbol: IrValueSymbol,
) : BaseVisitor<Pair<Name, ObjectMappingSource>?, Unit>(file) {

    override fun visitCall(expression: IrCall, data: Unit): Pair<Name, ObjectMappingSource>? {
        return when (expression.symbol.owner.name) {
            IDENTIFIER_MAPPED_FROM_PROPERTY, IDENTIFIER_MAPPED_FROM_CONSTANT -> {
                val target = expression.extensionReceiver!!.accept(TargetValueCollector(), Unit)
                val source = expression.valueArguments.first()!!.accept(SourceValueCollector(dispatchReceiverSymbol), Unit)

                target to source
            }
            IDENTIFIER_MAPPED_FROM_EXPRESSION -> {
                val target = expression.extensionReceiver!!.accept(TargetValueCollector(), Unit)
                val source = expression.valueArguments.first() as IrFunctionExpression

                target to ExpressionSource(
                    dispatchReceiverSymbol,
                    source,
                    (source.type as IrSimpleType).arguments[1].typeOrFail
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
                val type = transformation.type
                mapping.first to (mapping.second as PropertySource).copy(
                    transformation = transformation,
                    type = type,
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

        return when (expression.symbol.owner.name) {
            getterName(DataClassMapper<*, *>::forList.name) -> {
                val mapper = expression.symbol.owner.parent as IrClassImpl

                val function = mapper.functions
                    .filter { it.name == IDENTIFIER_MAP_LIST }
                    .first()

                function.symbol.wrap(expression.dispatchReceiver!!)
            }
            else -> {
                error("Expected get forList")
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
            IrStatementOrigin.ANONYMOUS_FUNCTION
        )
}

private class SourceValueCollector(
    private val dispatchReceiverSymbol: IrValueSymbol,
) : BaseVisitor<ObjectMappingSource, Unit>() {

    override fun visitPropertyReference(expression: IrPropertyReference, data: Unit): ObjectMappingSource {
        return PropertySource(
            property = expression.getter!!,
            type = (expression.type as IrSimpleType).arguments[1].typeOrFail,
            dispatchReceiverSymbol = dispatchReceiverSymbol,
            transformation = null,
            origin = expression,
            isResolvedAutomatically = false
        )
    }

    override fun visitConst(expression: IrConst<*>, data: Unit): ObjectMappingSource {
        return ConstantSource(expression.type, expression)
    }
}

private class TargetValueCollector : BaseVisitor<Name, Unit>() {

    override fun visitPropertyReference(expression: IrPropertyReference, data: Unit): Name {
        return expression.symbol.owner.name
    }
}