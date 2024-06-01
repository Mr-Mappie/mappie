package io.github.stefankoppier.mapping.resolving.classes

import io.github.stefankoppier.mapping.MappingPluginContext
import io.github.stefankoppier.mapping.resolving.BaseVisitor
import io.github.stefankoppier.mapping.resolving.ConstructorMappingTarget
import io.github.stefankoppier.mapping.resolving.MappingTarget
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.primaryConstructor

class TargetsCollector(pluginContext: MappingPluginContext) : BaseVisitor<MappingTarget, Unit>(pluginContext) {

    override fun visitFunction(declaration: IrFunction, data: Unit): MappingTarget {
        return declaration.returnType.getClass()!!.primaryConstructor!!.accept(this, Unit)
    }

    override fun visitConstructor(declaration: IrConstructor, data: Unit): MappingTarget {
        return ConstructorMappingTarget(declaration, declaration.valueParameters)
    }
}