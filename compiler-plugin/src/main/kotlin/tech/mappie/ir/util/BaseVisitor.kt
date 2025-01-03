package tech.mappie.ir.util

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor
import tech.mappie.exceptions.MappiePanicException.Companion.panic

abstract class BaseVisitor<R, D> : IrElementVisitor<R, D> {

    override fun visitElement(element: IrElement, data: D): R {
        panic("Unexpected element ${element.dump()}", element)
    }

    fun IrElement.accept(data: D): R =
        accept(this@BaseVisitor, data)
}