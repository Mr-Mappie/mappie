package io.github.mappie.resolving.classes

import io.github.mappie.MappiePluginContext
import io.github.mappie.resolving.BaseVisitor
import io.github.mappie.util.error
import io.github.mappie.util.location
import org.jetbrains.kotlin.ir.IrFileEntry
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.IrValueSymbol
import org.jetbrains.kotlin.ir.types.IrType
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
    pluginContext: MappiePluginContext,
    private val dispatchReceiverSymbol: IrValueSymbol,
    private val fileEntry: IrFileEntry,
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
                expression.valueArguments.first()?.accept(this, Unit) ?: emptyList()
            }
            else -> {
                pluginContext.messageCollector.error("map function must be defined via calling mapping", location(fileEntry, expression))
                error("")
            }
        }
    }

    override fun visitFunctionExpression(expression: IrFunctionExpression, data: Unit): List<Pair<Name, MappingSource>> {
        return expression.function.body!!.statements.map { it.accept(ObjectSourceCollector(pluginContext, dispatchReceiverSymbol), Unit) }
    }
}

private class ObjectSourceCollector(
    pluginContext: MappiePluginContext,
    private val dispatchReceiverSymbol: IrValueSymbol,
) : BaseVisitor<Pair<Name, MappingSource>, Unit>(pluginContext) {

    override fun visitCall(expression: IrCall, data: Unit): Pair<Name, MappingSource> {
        return when (expression.symbol.owner.name) {
            Name.identifier("property"), Name.identifier("constant") -> {
                val target = expression.extensionReceiver!!.accept(TargetValueCollector(pluginContext), Unit)
                val source = expression.valueArguments.first()!!.accept(SourceValueCollector(pluginContext, dispatchReceiverSymbol), Unit)

                target to source
            }
            Name.identifier("transform") -> {
                val mapping = expression.dispatchReceiver!!.accept(this, Unit)
                val transformation = expression.valueArguments.first()!! as IrFunctionExpression
                mapping.first to (mapping.second as PropertySource).copy(transformation = transformation)
            }
            else -> {
                TODO("$javaClass :: visitCall Not implemented for ${expression::class} :: ${expression.dump()}")
            }
        }
    }

    override fun visitTypeOperator(expression: IrTypeOperatorCall, data: Unit): Pair<Name, MappingSource> {
        return when (expression.operator.name) {
            "IMPLICIT_COERCION_TO_UNIT" -> expression.argument.accept(this, data)
            else -> error(expression.operator.name)
        }
    }
}

private class SourceValueCollector(
    pluginContext: MappiePluginContext,
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

private class TargetValueCollector(pluginContext: MappiePluginContext) : BaseVisitor<Name, Unit>(pluginContext) {

    override fun visitPropertyReference(expression: IrPropertyReference, data: Unit): Name {
        return expression.symbol.owner.name
    }
}