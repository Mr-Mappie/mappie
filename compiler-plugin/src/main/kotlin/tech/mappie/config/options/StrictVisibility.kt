package tech.mappie.config.options

import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.types.classOrFail
import org.jetbrains.kotlin.ir.util.getValueArgument
import org.jetbrains.kotlin.ir.util.isTrueConst
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.name.Name
import tech.mappie.MappieContext
import tech.mappie.util.CLASS_ID_USE_STRICT_VISIBILITY

fun MappieContext.useStrictVisibilityClassSymbol() =
    pluginContext.referenceClass(CLASS_ID_USE_STRICT_VISIBILITY)

fun MappieContext.getUseStrictVisibilityAnnotation(origin: IrFunction): IrConstructorCall? =
    origin.parentAsClass.annotations.firstOrNull { it.type.classOrFail == useStrictVisibilityClassSymbol() }

fun MappieContext.useStrictVisibility(origin: IrFunction): Boolean =
    getUseStrictVisibilityAnnotation(origin)
        ?.let { it.getValueArgument(Name.identifier("value"))?.isTrueConst() ?: true }
        ?: configuration.strictness.visibility