package io.github.mappie.resolving.enums

import io.github.mappie.BaseVisitor
import io.github.mappie.resolving.EnumMapping
import io.github.mappie.resolving.IDENTIFIER_MAPPING
import io.github.mappie.resolving.IDENTIFIER_MAPPED_FROM_ENUM_ENTRY
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.isEnumClass

class EnumMappingResolver(private val declaration: IrFunction) {

    private val targetType = declaration.returnType

    private val sourceType = declaration.valueParameters.first().type

    init {
        check(targetType.getClass()!!.isEnumClass)
    }

    fun resolve(): EnumMapping {
        val targets = targetType.getClass()!!.accept(EnumEntriesCollector(), Unit)
        val sources = sourceType.getClass()!!.accept(EnumEntriesCollector(), Unit)
        val explicitMappings = declaration.body!!.accept(EnumMappingsResolver(), Unit)

        val mappings = sources.associateWith { source ->
            explicitMappings.getOrElse(source) { targets.filter { target -> target.name == source.name } }
        }

        return EnumMapping(
            targetType = targetType,
            sourceType = sourceType,
            mappings = mappings,
        )
    }
}

private class EnumMappingsResolver : BaseVisitor<Map<IrEnumEntry, List<IrEnumEntry>>, Unit>() {

    override fun visitCall(expression: IrCall, data: Unit): Map<IrEnumEntry, List<IrEnumEntry>> {
        return when (expression.symbol.owner.name) {
            IDENTIFIER_MAPPING -> {
                expression.valueArguments.first()?.accept(data) ?: return emptyMap()
            }
            IDENTIFIER_MAPPED_FROM_ENUM_ENTRY -> {
                val target = (expression.extensionReceiver!! as IrGetEnumValue).symbol.owner
                val source = (expression.valueArguments.first()!! as IrGetEnumValue).symbol.owner
                mapOf(source to listOf(target))
            }
            else -> {
                super.visitCall(expression, data)
            }
        }
    }

    override fun visitTypeOperator(expression: IrTypeOperatorCall, data: Unit): Map<IrEnumEntry, List<IrEnumEntry>> {
        return expression.argument.accept(data)
    }

    override fun visitFunction(declaration: IrFunction, data: Unit): Map<IrEnumEntry, List<IrEnumEntry>> {
        return declaration.body!!.accept(data)
    }

    override fun visitReturn(expression: IrReturn, data: Unit): Map<IrEnumEntry, List<IrEnumEntry>> {
        return expression.value.accept(data)
    }

    override fun visitFunctionExpression(expression: IrFunctionExpression, data: Unit): Map<IrEnumEntry, List<IrEnumEntry>> {
        return expression.function.accept(data)
    }

    override fun visitBlockBody(body: IrBlockBody, data: Unit): Map<IrEnumEntry, List<IrEnumEntry>> {
        return body.statements.map { it.accept(data) }
            .fold(mutableMapOf()) { acc, current ->
                acc.apply {
                    current.forEach { (key, value) ->
                        merge(key, value, Collection<IrEnumEntry>::plus)
                    }
                }
            }
    }
}

