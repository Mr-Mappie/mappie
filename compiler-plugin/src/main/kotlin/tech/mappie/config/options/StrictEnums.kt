package tech.mappie.config.options

import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.types.classOrFail
import org.jetbrains.kotlin.ir.util.getValueArgument
import org.jetbrains.kotlin.ir.util.isTrueConst
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.name.Name
import tech.mappie.ir.MappieContext
import tech.mappie.util.CLASS_ID_USE_STRICT_ENUMS

context(context: MappieContext)
fun useStrictEnumsClassSymbol() =
    context.pluginContext.referenceClass(CLASS_ID_USE_STRICT_ENUMS)

context(context: MappieContext)
fun getUseStrictEnumsAnnotation(function: IrFunction): IrConstructorCall? =
    function.parentAsClass.annotations.firstOrNull { it.type.classOrFail == useStrictEnumsClassSymbol() }

context(context: MappieContext)
fun useStrictEnums(function: IrFunction): Boolean =
    getUseStrictEnumsAnnotation(function)
        ?.let { it.getValueArgument(Name.identifier("value"))?.isTrueConst() ?: true }
        ?: context.configuration.strictEnums