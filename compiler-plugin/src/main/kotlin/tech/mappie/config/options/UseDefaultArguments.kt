package tech.mappie.config.options

import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.types.classOrFail
import org.jetbrains.kotlin.ir.util.getValueArgument
import org.jetbrains.kotlin.ir.util.isTrueConst
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.name.Name
import tech.mappie.MappieContext
import tech.mappie.util.CLASS_ID_USE_DEFAULT_ARGUMENTS

fun MappieContext.useDefaultArgumentsClassSymbol() =
    pluginContext.referenceClass(CLASS_ID_USE_DEFAULT_ARGUMENTS)

fun MappieContext.getUseDefaultArgumentsAnnotation(origin: IrFunction): IrConstructorCall? =
    origin.parentAsClass.annotations.firstOrNull { it.type.classOrFail == useDefaultArgumentsClassSymbol() }

fun MappieContext.useDefaultArguments(origin: IrFunction): Boolean =
    getUseDefaultArgumentsAnnotation(origin)
        ?.let { it.getValueArgument(Name.identifier("value"))?.isTrueConst() ?: true }
        ?: configuration.useDefaultArguments
