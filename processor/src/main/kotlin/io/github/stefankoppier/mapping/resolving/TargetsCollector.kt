package io.github.stefankoppier.mapping.resolving

import io.github.stefankoppier.mapping.MappingPluginContext
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.primaryConstructor

sealed interface MappingTarget

data class ConstructorMappingTarget(
    val constructor: IrConstructor,
    val values: List<IrValueParameter>
) : MappingTarget

@OptIn(UnsafeDuringIrConstructionAPI::class)
class TargetsCollector(pluginContext: MappingPluginContext) : BaseVisitor<MappingTarget, Unit>(pluginContext) {

    override fun visitFunction(declaration: IrFunction, data: Unit): MappingTarget {
        return declaration.returnType.getClass()!!.primaryConstructor!!.accept(this, Unit)
    }

    override fun visitConstructor(declaration: IrConstructor, data: Unit): MappingTarget {
        return ConstructorMappingTarget(declaration, declaration.valueParameters)
    }
}