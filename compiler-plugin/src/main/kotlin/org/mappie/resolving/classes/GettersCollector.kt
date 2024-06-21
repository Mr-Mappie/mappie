package org.mappie.resolving.classes

import org.mappie.BaseVisitor
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.properties

class GettersCollector : org.mappie.BaseVisitor<List<IrSimpleFunction>, Unit>() {

    override fun visitValueParameter(declaration: IrValueParameter, data: Unit): List<IrSimpleFunction> {
        return declaration.type.getClass()!!.properties.flatMap { it.accept(Unit) }.toList()
    }

    override fun visitProperty(declaration: IrProperty, data: Unit): List<IrSimpleFunction> {
        return if (declaration.getter != null) declaration.getter?.let { listOf(it) } ?: emptyList() else emptyList()
    }
}