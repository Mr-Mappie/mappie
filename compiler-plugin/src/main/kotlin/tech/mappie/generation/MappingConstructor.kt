package tech.mappie.generation

import org.jetbrains.kotlin.ir.builders.Scope
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.util.fileEntry

abstract class MappingConstructor(
    protected val declaration: IrFunction,
) {

    protected val file = declaration.fileEntry

    abstract fun construct(scope: Scope): IrBody
}