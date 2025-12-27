package tech.mappie.config.options

import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.types.classOrFail
import org.jetbrains.kotlin.ir.util.getValueArgument
import org.jetbrains.kotlin.ir.util.isTrueConst
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.name.Name
import tech.mappie.ir.MappieContext
import tech.mappie.util.CLASS_ID_USE_CASE_INSENSITIVE_MATCHING

context(context: MappieContext)
fun useCaseInsensitiveMatchingClassSymbol() =
    context.pluginContext.referenceClass(CLASS_ID_USE_CASE_INSENSITIVE_MATCHING)

context(context: MappieContext)
fun getCaseInsensitiveMatchingAnnotation(origin: IrFunction): IrConstructorCall? =
    origin.parentAsClass.annotations.firstOrNull { it.type.classOrFail == useCaseInsensitiveMatchingClassSymbol() }

context(context: MappieContext)
fun useCaseInsensitiveMatching(origin: IrFunction): Boolean =
    getCaseInsensitiveMatchingAnnotation(origin)
        ?.let { it.getValueArgument(Name.identifier("value"))?.isTrueConst() ?: true }
        ?: context.configuration.useCaseInsensitiveMatching
