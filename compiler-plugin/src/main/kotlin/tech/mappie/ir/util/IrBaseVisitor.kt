package tech.mappie.ir.util

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import tech.mappie.ir.exceptions.MappiePanicException

abstract class IrBaseVisitor<R, D> : IrElementVisitor<R, D> {

    override fun visitElement(element: IrElement, data: D): R {
        throw MappiePanicException("Unexpected element ${element.dump()}")
    }

    fun IrElement.accept(data: D): R =
        accept(this@IrBaseVisitor, data)
}