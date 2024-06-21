package org.mappie.resolving.classes

import org.mappie.BaseVisitor
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.constructors

class ConstructorsCollector : org.mappie.BaseVisitor<List<IrConstructor>, Unit>() {

    override fun visitFunction(declaration: IrFunction, data: Unit): List<IrConstructor> {
        return declaration.returnType.getClass()!!.constructors.toList()
    }
}