package org.mappie.resolving.primitives

import org.mappie.resolving.SingleValueMapping
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.types.isPrimitiveType
import org.jetbrains.kotlin.ir.types.isString

class PrimitiveResolver(private val declaration: IrFunction) {

    private val targetType = declaration.returnType

    init {
        check(targetType.isPrimitiveType() || targetType.isString())
    }

    fun resolve(): SingleValueMapping {
        return SingleValueMapping(targetType, declaration.body!!.accept(PrimitiveBodyCollector(declaration), Unit))
    }
}
