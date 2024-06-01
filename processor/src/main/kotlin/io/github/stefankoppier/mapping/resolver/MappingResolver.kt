package io.github.stefankoppier.mapping.resolver

import io.github.stefankoppier.mapping.MappingPluginContext
import io.github.stefankoppier.mapping.util.error
import io.github.stefankoppier.mapping.util.location
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.IrValueSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

sealed interface MappingSource

data class PropertySource(
    val property: IrSimpleFunctionSymbol,
    val type: IrType,
    val dispatchReceiverSymbol: IrValueSymbol,
) : MappingSource

data class ConstantSource<T>(
    val type: IrType,
    val value: T
) : MappingSource

@OptIn(UnsafeDuringIrConstructionAPI::class)
class MappingResolver(private val pluginContext: MappingPluginContext) : IrElementVisitor<MutableList<MappingSource>, MutableList<MappingSource>> {

    override fun visitFunction(declaration: IrFunction, data: MutableList<MappingSource>): MutableList<MappingSource> {
        val sourceParameter = requireNotNull(declaration.valueParameters.firstOrNull())
        val mappingTarget = declaration.accept(TargetsCollector(), Unit) as ConstructorMappingTarget
        val sourceClass = requireNotNull(sourceParameter.type.getClass()) {
            "Expected type of source argument to be non-null."
        }

        val sources: MutableList<MappingSource> = mappingTarget.values.map { target ->
            val source = requireNotNull(sourceClass.properties.firstOrNull { it.name == target.name }) {
                pluginContext.messageCollector.error("Target ${target.name.asString()} has no source defined", location(declaration))
            }
            PropertySource(
                sourceClass.getPropertyGetter(source.name.asString())!!,
                target.type,
                sourceParameter.symbol,
            )
        }.toMutableList()

        return sources
    }

    override fun visitElement(element: IrElement, data: MutableList<MappingSource>): MutableList<MappingSource> {
        TODO("javaClass Not implemented for ${element::class} :: ${element.dump()}")
    }
}
