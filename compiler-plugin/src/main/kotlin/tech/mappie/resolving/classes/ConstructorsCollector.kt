package tech.mappie.resolving.classes

import org.jetbrains.kotlin.ir.IrFileEntry
import tech.mappie.BaseVisitor
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.constructors

class ConstructorsCollector(file: IrFileEntry) : BaseVisitor<List<IrConstructor>, Unit>(file) {

    override fun visitFunction(declaration: IrFunction, data: Unit): List<IrConstructor> {
        return declaration.returnType.getClass()!!.constructors.toList()
    }
}