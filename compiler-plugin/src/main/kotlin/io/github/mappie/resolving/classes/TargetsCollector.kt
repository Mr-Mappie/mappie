package io.github.mappie.resolving.classes

import io.github.mappie.BaseVisitor
import io.github.mappie.resolving.ConstructorMappingTarget
import io.github.mappie.resolving.MappingTarget
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.primaryConstructor

class TargetsCollector : BaseVisitor<MappingTarget, Unit> {

    override fun visitFunction(declaration: IrFunction, data: Unit): MappingTarget {
        return declaration.returnType.getClass()!!.primaryConstructor!!.accept(this, Unit)
    }

    override fun visitConstructor(declaration: IrConstructor, data: Unit): MappingTarget {
        return ConstructorMappingTarget(declaration, declaration.valueParameters)
    }
}

