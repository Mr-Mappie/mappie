package tech.mappie.util

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import tech.mappie.exceptions.MappiePanicException

abstract class BaseVisitor<R, D> : IrElementVisitor<R, D> {

    override fun visitElement(element: IrElement, data: D): R {
        throw MappiePanicException("Unexpected element ${element.dump()}")
    }

    fun IrElement.accept(data: D): R =
        accept(this@BaseVisitor, data)
}