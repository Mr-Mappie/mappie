package io.github.stefankoppier.mapping.resolving

import io.github.stefankoppier.mapping.MappingPluginContext
import io.github.stefankoppier.mapping.resolving.classes.ClassMappingResolver
import io.github.stefankoppier.mapping.resolving.classes.MappingSource
import io.github.stefankoppier.mapping.resolving.enums.EnumMappingResolver
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.*

sealed interface Mapping

data class ConstructorCallMapping(
    val sources: List<MappingSource>
) : Mapping

data class EnumMapping(
    val targets: List<IrEnumEntry>,
    val targetType: IrType,
    val sources: List<IrEnumEntry>,
    val sourceType: IrType,
) : Mapping

class MappingResolver(pluginContext: MappingPluginContext)
    : BaseVisitor<Mapping, Unit>(pluginContext) {

    override fun visitFunction(declaration: IrFunction, data: Unit): Mapping {
        val target = declaration.returnType.getClass()!!
        return when {
            target.isData -> declaration.accept(ClassMappingResolver(pluginContext), Unit)
            target.isEnumClass -> declaration.accept(EnumMappingResolver(pluginContext), Unit)
            else -> error("Target not supported yet.")
        }
    }
}
