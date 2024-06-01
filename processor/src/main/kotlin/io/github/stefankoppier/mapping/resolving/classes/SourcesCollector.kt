package io.github.stefankoppier.mapping.resolving.classes

import io.github.stefankoppier.mapping.MappingPluginContext
import io.github.stefankoppier.mapping.resolving.BaseVisitor
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.IrValueSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.statements
import org.jetbrains.kotlin.name.Name

sealed interface MappingSource

data class PropertySource(
    val property: IrSimpleFunctionSymbol,
    val type: IrType,
    val dispatchReceiverSymbol: IrValueSymbol,
) : MappingSource

data class ConstantSource<T>(
    val type: IrType,
    val value: IrConst<T>,
) : MappingSource

class ObjectSourcesCollector(
    pluginContext: MappingPluginContext,
    private val dispatchReceiverSymbol: IrValueSymbol,
) : BaseVisitor<List<Pair<Name, MappingSource>>, Unit>(pluginContext) {

    override fun visitBlockBody(body: IrBlockBody, data: Unit): List<Pair<Name, MappingSource>> {
        require(body.statements.size == 1)
        return body.statements.first().accept(this, Unit)
    }

    override fun visitReturn(expression: IrReturn, data: Unit): List<Pair<Name, MappingSource>> {
        return expression.value.accept(this, Unit)
    }

    override fun visitCall(expression: IrCall, data: Unit): List<Pair<Name, MappingSource>> {
        return when (expression.symbol.owner.name) {
            Name.identifier("mapping") -> {
                expression.valueArguments.first()!!.accept(ObjectSourcesCollector(pluginContext, dispatchReceiverSymbol), Unit)
            }
            Name.identifier("property"), Name.identifier("constant") -> {
                val target = expression.extensionReceiver!!.accept(TargetValueCollector(pluginContext), Unit)
                val source = expression.valueArguments[0]!!.accept(SourceValueCollector(pluginContext, dispatchReceiverSymbol), Unit)

                listOf(target to source)
            }
            else -> {
                TODO("$javaClass :: visitCall Not implemented for ${expression::class} :: ${expression.dump()}")
            }
        }
    }

    override fun visitTypeOperator(expression: IrTypeOperatorCall, data: Unit): List<Pair<Name, MappingSource>> {
        return when (expression.operator.name) {
            "IMPLICIT_COERCION_TO_UNIT" -> expression.argument.accept(this, Unit)
            else -> error(expression.operator.name)
        }
    }

    override fun visitFunctionExpression(expression: IrFunctionExpression, data: Unit): List<Pair<Name, MappingSource>> {
        return expression.function.body!!.statements.flatMap { it.accept(this, Unit) }
    }
}

private class SourceValueCollector(
    pluginContext: MappingPluginContext,
    private val dispatchReceiverSymbol: IrValueSymbol
) : BaseVisitor<MappingSource, Unit>(pluginContext) {

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

private class TargetValueCollector(pluginContext: MappingPluginContext) : BaseVisitor<Name, Unit>(pluginContext) {

    override fun visitPropertyReference(expression: IrPropertyReference, data: Unit): Name {
        return expression.symbol.owner.name
    }
}