package io.github.stefankoppier.mapping.resolver

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.IrValueSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.getPropertyGetter
import org.jetbrains.kotlin.ir.util.properties
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
class MappingResolver : IrElementVisitor<MutableList<MappingSource>, MutableList<MappingSource>> {

    override fun visitFunction(declaration: IrFunction, data: MutableList<MappingSource>): MutableList<MappingSource> {
        val source = requireNotNull(declaration.valueParameters.firstOrNull())

        val target = declaration.accept(TargetsCollector(), Unit) as ConstructorMappingTarget

        val sourceClass = requireNotNull(source.type.getClass()) {
            "Expected type of source argument to be non-null."
        }
        val sourceValues = sourceClass.properties

        val sources: MutableList<MappingSource> = target.values.map { target ->
            PropertySource(
                sourceClass.getPropertyGetter(sourceValues.first { it.name == target.name }.name.asString())!!,
                target.type,
                source.symbol,
            )
        }.toMutableList()

        return sources
    }

    override fun visitElement(element: IrElement, data: MutableList<MappingSource>): MutableList<MappingSource> {
        TODO("javaClass Not implemented for ${element::class} :: ${element.dump()}")
    }
}
