package io.github.stefankoppier.mapping.resolving

import io.github.stefankoppier.mapping.MappingPluginContext
import io.github.stefankoppier.mapping.resolving.classes.ClassMappingResolver
import io.github.stefankoppier.mapping.resolving.classes.MappingSource
import io.github.stefankoppier.mapping.resolving.enums.EnumMappingResolver
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.*

sealed interface Mapping

data class ConstructorCallMapping(
    val sources: List<MappingSource>
) : Mapping

data class EnumMapping(
    val targetType: IrType,
    val sourceType: IrType,
    val mappings: Map<IrEnumEntry, List<IrEnumEntry>>,
) : Mapping

data class SingleValueMapping(
    val type: IrType,
    val value: IrExpression,
) : Mapping

class MappingResolver(pluginContext: MappingPluginContext)
    : BaseVisitor<Mapping, Unit>(pluginContext) {

    override fun visitFunction(declaration: IrFunction, data: Unit): Mapping {
        val target = declaration.returnType.getClass()!!
        return when {
            target.isEnumClass -> declaration.accept(EnumMappingResolver(pluginContext), Unit)
            else -> declaration.accept(ClassMappingResolver(pluginContext), Unit)
        }
    }
}
