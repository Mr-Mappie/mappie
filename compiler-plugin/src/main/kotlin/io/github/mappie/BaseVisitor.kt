package io.github.mappie

import io.github.mappie.MappieIrRegistrar.Companion.context
import io.github.mappie.util.error
import io.github.mappie.util.location
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrFileEntry
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

abstract class BaseVisitor<R, D>(protected var file: IrFileEntry? = null) : IrElementVisitor<R, D> {

    override fun visitElement(element: IrElement, data: D): R {
        context.messageCollector.error("Unexpected element", file?.let { location(it, element) })
        error("${javaClass.simpleName} Not implemented for ${element::class} :: ${element.dump()}")
    }

    fun IrElement.accept(data: D): R =
        accept(this@BaseVisitor, data)
}