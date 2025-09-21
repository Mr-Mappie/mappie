package tech.mappie.config.options

import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.types.classOrFail
import org.jetbrains.kotlin.ir.util.getValueArgument
import org.jetbrains.kotlin.ir.util.isTrueConst
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.name.Name
import tech.mappie.MappieContext
import tech.mappie.util.CLASS_ID_USE_STRICT_JAVA_NULLABILITY

fun MappieContext.useStrictJavaNullabilityClassSymbol() =
    pluginContext.referenceClass(CLASS_ID_USE_STRICT_JAVA_NULLABILITY)

fun MappieContext.getUseStrictJavaNullabilityAnnotation(origin: IrFunction): IrConstructorCall? =
    origin.parentAsClass.annotations.firstOrNull { it.type.classOrFail == useStrictJavaNullabilityClassSymbol() }

fun MappieContext.useStrictJavaNullability(origin: IrFunction): Boolean =
    getUseStrictJavaNullabilityAnnotation(origin)
        ?.let { it.getValueArgument(Name.identifier("value"))?.isTrueConst() ?: true }
        ?: configuration.strictJavaNullability