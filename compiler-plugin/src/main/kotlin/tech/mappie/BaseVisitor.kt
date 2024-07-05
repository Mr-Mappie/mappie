package tech.mappie

import tech.mappie.util.location
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrFileEntry
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

abstract class BaseVisitor<R, D>(protected var file: IrFileEntry?) : IrElementVisitor<R, D> {

    override fun visitElement(element: IrElement, data: D): R {
        mappieTerminate("Unexpected element", file?.let { location(it, element) })
    }

    fun IrElement.accept(data: D): R =
        accept(this@BaseVisitor, data)
}