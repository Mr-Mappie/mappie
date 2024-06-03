package io.github.mappie.resolving.classes

import io.github.mappie.BaseVisitor
import io.github.mappie.resolving.ConstructorMappingTarget
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.primaryConstructor

class TargetsCollector : BaseVisitor<ConstructorMappingTarget, Unit> {

    override fun visitFunction(declaration: IrFunction, data: Unit): ConstructorMappingTarget {
        return declaration.returnType.getClass()!!.primaryConstructor!!.accept(this, Unit)
    }

    override fun visitConstructor(declaration: IrConstructor, data: Unit): ConstructorMappingTarget {
        return ConstructorMappingTarget(declaration, declaration.valueParameters)
    }
}

