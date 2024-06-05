package io.github.mappie.resolving.classes

import io.github.mappie.BaseVisitor
import io.github.mappie.MappieIrRegistrar.Companion.context
import io.github.mappie.resolving.*
import io.github.mappie.util.irGet
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.declarations.createExpressionBody
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.expressions.impl.IrCallImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrFunctionExpressionImpl
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.IrValueSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.Name

sealed interface MappingSource

data class PropertySource(
    val property: IrSimpleFunctionSymbol,
    val type: IrType,
    val dispatchReceiverSymbol: IrValueSymbol,
    val transformation: IrFunctionExpression? = null,
) : MappingSource

data class ConstantSource<T>(
    val type: IrType,
    val value: IrConst<T>,
) : MappingSource

class ObjectSourcesCollector(
    private val dispatchReceiverSymbol: IrValueSymbol
) : BaseVisitor<List<Pair<Name, MappingSource>>, Unit> {

    override fun visitBlockBody(body: IrBlockBody, data: Unit): List<Pair<Name, MappingSource>> {
        return body.statements.single().accept(data)
    }

    override fun visitReturn(expression: IrReturn, data: Unit): List<Pair<Name, MappingSource>> {
        return expression.value.accept(data)
    }

    override fun visitCall(expression: IrCall, data: Unit): List<Pair<Name, MappingSource>> {
        return when (expression.symbol.owner.name) {
            IDENTIFIER_MAPPING -> {
                expression.valueArguments.first()?.accept(data) ?: emptyList()
            }
            else -> {
                emptyList()
            }
        }
    }

    override fun visitFunctionExpression(expression: IrFunctionExpression, data: Unit): List<Pair<Name, MappingSource>> {
        return expression.function.body!!.statements.map { it.accept(ObjectSourceCollector(dispatchReceiverSymbol), Unit) }
    }
}

private class ObjectSourceCollector(
    private val dispatchReceiverSymbol: IrValueSymbol,
) : BaseVisitor<Pair<Name, MappingSource>, Unit> {

    override fun visitCall(expression: IrCall, data: Unit): Pair<Name, MappingSource> {
        return when (expression.symbol.owner.name) {
            IDENTIFIER_MAPPED_FROM_PROPERTY, IDENTIFIER_MAPPED_FROM_CONSTANT -> {
                val target = expression.extensionReceiver!!.accept(TargetValueCollector(), Unit)
                val source = expression.valueArguments.first()!!.accept(SourceValueCollector(dispatchReceiverSymbol), Unit)

                target to source
            }
            IDENTIFIER_TRANFORM -> {
                val mapping = expression.dispatchReceiver!!.accept(data)
                val transformation = expression.valueArguments.first()!! as IrFunctionExpression
                mapping.first to (mapping.second as PropertySource).copy(transformation = transformation)
            }
            IDENTIFIER_VIA -> {
                val mapping = expression.dispatchReceiver!!.accept(data)
                val transformation = expression.valueArguments.first()!!.accept(MapperReferenceCollector(), Unit)
                mapping.first to (mapping.second as PropertySource).copy(transformation = transformation)
            }
            else -> {
                TODO("$javaClass :: visitCall Not implemented for ${expression::class} :: ${expression.dump()}")
            }
        }
    }

    override fun visitTypeOperator(expression: IrTypeOperatorCall, data: Unit): Pair<Name, MappingSource> {
        return when (expression.operator.name) {
            "IMPLICIT_COERCION_TO_UNIT" -> expression.argument.accept(data)
            else -> error(expression.operator.name)
        }
    }
}

private class MapperReferenceCollector : BaseVisitor<IrFunctionExpression, Unit> {

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
    private val dispatchReceiverSymbol: IrValueSymbol
) : BaseVisitor<MappingSource, Unit> {

    override fun visitPropertyReference(expression: IrPropertyReference, data: Unit): MappingSource {
        return PropertySource(
            property = expression.getter!!,
            type = expression.type,
            dispatchReceiverSymbol = dispatchReceiverSymbol,
        )
    }

    override fun visitConst(expression: IrConst<*>, data: Unit): MappingSource {
        return ConstantSource(expression.type, expression)
    }
}

private class TargetValueCollector : BaseVisitor<Name, Unit> {

    override fun visitPropertyReference(expression: IrPropertyReference, data: Unit): Name {
        return expression.symbol.owner.name
    }
}