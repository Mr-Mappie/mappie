package tech.mappie.ir_old.util

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.visitors.IrVisitor
import tech.mappie.exceptions.MappiePanicException.Companion.panic

abstract class BaseVisitor<R, D> : IrVisitor<R, D>() {

    override fun visitElement(element: IrElement, data: D): R {
        panic("Unexpected element ${element.dump()}", element)
    }

    fun IrElement.accept(data: D): R =
        accept(this@BaseVisitor, data)
}