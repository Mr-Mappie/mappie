package io.github.mappie

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

interface BaseVisitor<R, D> : IrElementVisitor<R, D> {

    override fun visitElement(element: IrElement, data: D): R {
        error("${javaClass.simpleName} Not implemented for ${element::class} :: ${element.dump()}")
    }

    fun IrElement.accept(data: D): R =
        accept(this@BaseVisitor, data)
}