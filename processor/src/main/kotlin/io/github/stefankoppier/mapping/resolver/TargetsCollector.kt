package io.github.stefankoppier.mapping.resolver

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.primaryConstructor
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

sealed interface MappingTarget

data class ConstructorMappingTarget(
    val constructor: IrConstructor,
    val values: List<IrValueParameter>
) : MappingTarget

@OptIn(UnsafeDuringIrConstructionAPI::class)
class TargetsCollector : IrElementVisitor<MappingTarget, Unit> {

    override fun visitFunction(declaration: IrFunction, data: Unit): MappingTarget {
        return declaration.returnType.getClass()!!.primaryConstructor!!.accept(this, Unit)
    }

    override fun visitConstructor(declaration: IrConstructor, data: Unit): MappingTarget {
        return ConstructorMappingTarget(declaration, declaration.valueParameters)
    }

    override fun visitElement(element: IrElement, data: Unit): MappingTarget {
        TODO("$javaClass Not implemented for ${element::class} :: ${element.dump()}")
    }
}