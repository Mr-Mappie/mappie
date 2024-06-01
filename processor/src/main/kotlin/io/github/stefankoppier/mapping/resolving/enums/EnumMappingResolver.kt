package io.github.stefankoppier.mapping.resolving.enums

import io.github.stefankoppier.mapping.MappingPluginContext
import io.github.stefankoppier.mapping.resolving.BaseVisitor
import io.github.stefankoppier.mapping.resolving.EnumMapping
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.types.getClass

class EnumMappingResolver(pluginContext: MappingPluginContext)
    : BaseVisitor<EnumMapping, Unit>(pluginContext) {

    override fun visitFunction(declaration: IrFunction, data: Unit): EnumMapping {
        val sourceType = declaration.valueParameters.first().type
        return EnumMapping(
            targets = declaration.returnType.getClass()!!.accept(EnumMappingTargetsCollector(pluginContext), Unit),
            targetType = declaration.returnType,
            sources = sourceType.getClass()!!.accept(EnumMappingTargetsCollector(pluginContext), Unit),
            sourceType = sourceType
        )
    }
}

class EnumMappingTargetsCollector(pluginContext: MappingPluginContext)
    : BaseVisitor<List<IrEnumEntry>, Unit>(pluginContext) {

    override fun visitClass(declaration: IrClass, data: Unit): List<IrEnumEntry> {
        return declaration.declarations
            .filterIsInstance<IrEnumEntry>()
            .flatMap { it.accept(this, Unit) }
    }

    override fun visitEnumEntry(declaration: IrEnumEntry, data: Unit): List<IrEnumEntry> {
        return listOf(declaration)
    }
}