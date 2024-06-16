package io.github.mappie.resolving.enums

import io.github.mappie.BaseVisitor
import io.github.mappie.MappieIrRegistrar.Companion.context
import io.github.mappie.resolving.EnumMapping
import io.github.mappie.resolving.IDENTIFIER_MAPPING
import io.github.mappie.resolving.IDENTIFIER_MAPPED_FROM_ENUM_ENTRY
import io.github.mappie.util.location
import io.github.mappie.util.warn
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.callableId
import org.jetbrains.kotlin.ir.util.fileEntry
import org.jetbrains.kotlin.ir.util.isEnumClass

class EnumResolver(private val declaration: IrFunction) {

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
            val resolvedMapping = targets.filter { target -> target.name == source.name }
            val explicitMapping = explicitMappings[source]
            if (resolvedMapping.isNotEmpty() && explicitMapping != null) {
                with (explicitMapping.first()) {
                    context.messageCollector.warn("Unnecessary explicit mapping of ${target.symbol.owner.callableId.className}.${target.name.asString()}", location(declaration.fileEntry, origin))
                }
            }
            explicitMapping?.map { it.target } ?: resolvedMapping
        }

        return EnumMapping(
            targetType = targetType,
            sourceType = sourceType,
            mappings = mappings,
        )
    }
}

data class ExplicitEnumMapping(
    val target: IrEnumEntry,
    val origin: IrExpression,
)

private class EnumMappingsResolver : BaseVisitor<Map<IrEnumEntry, List<ExplicitEnumMapping>>, Unit>() {

    override fun visitCall(expression: IrCall, data: Unit): Map<IrEnumEntry, List<ExplicitEnumMapping>> {
        return when (expression.symbol.owner.name) {
            IDENTIFIER_MAPPING -> {
                expression.valueArguments.first()?.accept(data) ?: return emptyMap()
            }
            IDENTIFIER_MAPPED_FROM_ENUM_ENTRY -> {
                val target = (expression.extensionReceiver!! as IrGetEnumValue).symbol.owner
                val source = (expression.valueArguments.first()!! as IrGetEnumValue).symbol.owner
                mapOf(source to listOf(ExplicitEnumMapping(target, expression)))
            }
            else -> {
                super.visitCall(expression, data)
            }
        }
    }

    override fun visitTypeOperator(expression: IrTypeOperatorCall, data: Unit): Map<IrEnumEntry, List<ExplicitEnumMapping>> {
        return expression.argument.accept(data)
    }

    override fun visitFunction(declaration: IrFunction, data: Unit): Map<IrEnumEntry, List<ExplicitEnumMapping>> {
        return declaration.body!!.accept(data)
    }

    override fun visitReturn(expression: IrReturn, data: Unit): Map<IrEnumEntry, List<ExplicitEnumMapping>> {
        return expression.value.accept(data)
    }

    override fun visitFunctionExpression(expression: IrFunctionExpression, data: Unit): Map<IrEnumEntry, List<ExplicitEnumMapping>> {
        return expression.function.accept(data)
    }

    override fun visitBlockBody(body: IrBlockBody, data: Unit): Map<IrEnumEntry, List<ExplicitEnumMapping>> {
        return body.statements.map { it.accept(data) }
            .fold(mutableMapOf()) { acc, current ->
                acc.apply {
                    current.forEach { (key, value) ->
                        merge(key, value, Collection<ExplicitEnumMapping>::plus)
                    }
                }
            }
    }
}