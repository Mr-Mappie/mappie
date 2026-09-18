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

    @JvmField
    val QUOTED_STRINGS = Renderer<List<String>> { values ->
        values.joinToReadableQuotedString()
    }

    @JvmField
    val INDENTED_LINES = Renderer<List<String>> { values ->
        values.joinToString(System.lineSeparator()) { "\t" + it }
    }
}

fun List<String>.joinToReadableQuotedString(): String = when {
    size > 5 -> take(5).joinToString { "'$it'" } + " and ${size - 5} more"
    size > 1 -> dropLast(1).joinToString { "'$it'" } + " and '${last()}'"
    size == 1 -> "'${single()}'"
    else -> ""
}