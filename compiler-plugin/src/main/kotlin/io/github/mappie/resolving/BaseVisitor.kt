package io.github.mappie.resolving

import io.github.mappie.MappiePluginContext
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

abstract class BaseVisitor<R, D>(protected val pluginContext: MappiePluginContext) : IrElementVisitor<R, D> {

    override fun visitElement(element: IrElement, data: D): R {
        error("${javaClass.simpleName} Not implemented for ${element::class} :: ${element.dump()}")
    }
}