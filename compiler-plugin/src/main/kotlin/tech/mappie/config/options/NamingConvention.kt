package tech.mappie.config.options

import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.expressions.IrGetEnumValue
import org.jetbrains.kotlin.ir.types.classOrFail
import org.jetbrains.kotlin.ir.util.getValueArgument
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.name.Name
import tech.mappie.ir.MappieContext
import tech.mappie.util.CLASS_ID_USE_NAMING_CONVENTION

/**
 * Internal representation of the naming convention mode.
 */
enum class NamingConventionMode {
    STRICT,
    LENIENT,
}

context(context: MappieContext)
fun useNamingConventionClassSymbol() =
    context.pluginContext.referenceClass(CLASS_ID_USE_NAMING_CONVENTION)

context(context: MappieContext)
fun getNamingConventionAnnotation(origin: IrFunction): IrConstructorCall? =
    origin.parentAsClass.annotations.firstOrNull { it.type.classOrFail == useNamingConventionClassSymbol() }

context(context: MappieContext)
fun namingConvention(origin: IrFunction): NamingConventionMode {
    val annotation = getNamingConventionAnnotation(origin)
    if (annotation != null) {
        val valueArg = annotation.getValueArgument(Name.identifier("value"))
        if (valueArg is IrGetEnumValue) {
            return when (valueArg.symbol.owner.name.asString()) {
                "STRICT" -> NamingConventionMode.STRICT
                "LENIENT" -> NamingConventionMode.LENIENT
                else -> NamingConventionMode.LENIENT
            }
        }
        // Default when annotation is present but no explicit value
        return NamingConventionMode.LENIENT
    }
    return context.configuration.namingConvention
}
