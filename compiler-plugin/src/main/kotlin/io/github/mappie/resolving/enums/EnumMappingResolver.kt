package io.github.mappie.resolving.enums

import io.github.mappie.BaseVisitor
import io.github.mappie.resolving.EnumMapping
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.isEnumClass
import org.jetbrains.kotlin.name.Name

class EnumMappingResolver : BaseVisitor<EnumMapping, Unit> {

    override fun visitFunction(declaration: IrFunction, data: Unit): EnumMapping {
        val targetType = declaration.returnType
        check(targetType.getClass()!!.isEnumClass)
        val sourceType = declaration.valueParameters.first().type
        val targets = targetType.getClass()!!.accept(EnumEntriesCollector(), Unit)
        val resolvedSources = sourceType.getClass()!!.accept(EnumEntriesCollector(), Unit)
        val explicitSources = declaration.body!!.accept(EnumMappingsResolver(), Unit)
        val mappings = targets.associateWith { target ->
            explicitSources.getOrElse(target) { resolvedSources.filter { source -> target.name == source.name } }
        }
        return EnumMapping(
            targetType = targetType,
            sourceType = sourceType,
            mappings = mappings,
        )
    }
}

private class EnumMappingsResolver : BaseVisitor<Map<IrEnumEntry, List<IrEnumEntry>>, Unit> {

    override fun visitCall(expression: IrCall, data: Unit): Map<IrEnumEntry, List<IrEnumEntry>> {
        return when (expression.symbol.owner.name) {
            Name.identifier("enumMapping") -> {
                expression.valueArguments.first()?.accept(this, Unit) ?: return emptyMap()
            }
            Name.identifier("value") -> {
                val target = (expression.extensionReceiver!! as IrGetEnumValue).symbol.owner
                val source = (expression.valueArguments.first()!! as IrGetEnumValue).symbol.owner
                mapOf(target to listOf(source))
            }
            else -> {
                error("Unexpected symbol ${expression.symbol.owner.name}")
            }
        }
    }

    override fun visitTypeOperator(expression: IrTypeOperatorCall, data: Unit): Map<IrEnumEntry, List<IrEnumEntry>> {
        return expression.argument.accept(this, Unit)
    }

    override fun visitFunction(declaration: IrFunction, data: Unit): Map<IrEnumEntry, List<IrEnumEntry>> {
        return declaration.body!!.accept(this, Unit)
    }

    override fun visitReturn(expression: IrReturn, data: Unit): Map<IrEnumEntry, List<IrEnumEntry>> {
        return expression.value.accept(this, Unit)
    }

    override fun visitFunctionExpression(expression: IrFunctionExpression, data: Unit): Map<IrEnumEntry, List<IrEnumEntry>> {
        return expression.function.accept(this, Unit)
    }

    override fun visitBlockBody(body: IrBlockBody, data: Unit): Map<IrEnumEntry, List<IrEnumEntry>> {
        return body.statements.single().accept(this, Unit)
    }
}

