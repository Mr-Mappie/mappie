package io.github.mappie.resolving.classes

import io.github.mappie.BaseVisitor
import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.primaryConstructor

class ValueParametersCollector : BaseVisitor<List<IrValueParameter>, Unit> {

    override fun visitFunction(declaration: IrFunction, data: Unit): List<IrValueParameter> {
        return declaration.returnType.getClass()!!.primaryConstructor!!.accept(data)
    }

    override fun visitConstructor(declaration: IrConstructor, data: Unit): List<IrValueParameter> {
        return declaration.valueParameters
    }
}

