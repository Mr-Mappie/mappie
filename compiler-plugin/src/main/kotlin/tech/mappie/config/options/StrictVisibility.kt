package tech.mappie.config.options

import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.types.classOrFail
import org.jetbrains.kotlin.ir.util.isTrueConst
import org.jetbrains.kotlin.ir.util.parentAsClass
import tech.mappie.ir.MappieContext
import tech.mappie.util.CLASS_ID_USE_STRICT_VISIBILITY

context(context: MappieContext)
fun useStrictVisibilityClassSymbol() =
    context.pluginContext.finderForBuiltins().findClass(CLASS_ID_USE_STRICT_VISIBILITY)

context(context: MappieContext)
fun getUseStrictVisibilityAnnotation(origin: IrFunction): IrConstructorCall? =
    origin.parentAsClass.annotations.firstOrNull { it.type.classOrFail == useStrictVisibilityClassSymbol() }

context(context: MappieContext)
fun useStrictVisibility(origin: IrFunction): Boolean =
    getUseStrictVisibilityAnnotation(origin)
        ?.let { it.arguments.first()?.isTrueConst() ?: true }
        ?: context.configuration.strictVisibility