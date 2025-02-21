package tech.mappie.config.options

import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.types.classOrFail
import org.jetbrains.kotlin.ir.util.getValueArgument
import org.jetbrains.kotlin.ir.util.isTrueConst
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.name.Name
import tech.mappie.MappieContext
import tech.mappie.util.CLASS_ID_USE_STRICT_ENUMS

fun MappieContext.useStrictEnumsClassSymbol() =
    pluginContext.referenceClass(CLASS_ID_USE_STRICT_ENUMS)

fun MappieContext.getUseStrictEnumsAnnotation(function: IrFunction): IrConstructorCall? =
    function.parentAsClass.annotations.firstOrNull { it.type.classOrFail == useStrictEnumsClassSymbol() }

fun MappieContext.useStrictEnums(function: IrFunction): Boolean =
    getUseStrictEnumsAnnotation(function)
        ?.let { it.getValueArgument(Name.identifier("value"))?.isTrueConst() ?: true }
        ?: configuration.strictEnums