package tech.mappie.util

import org.jetbrains.kotlin.diagnostics.rendering.Renderer
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classOrNull
import org.jetbrains.kotlin.ir.util.dumpKotlinLike

object MappieRenderers {

    @JvmField
    val IR_TYPE = Renderer<IrType> {
        it.classOrNull?.owner?.name?.asString() ?: it.dumpKotlinLike()
    }

    @JvmField
    val IR_CLASS = Renderer<IrClassSymbol> { clazz ->
        clazz.owner.name.asString()
    }
}