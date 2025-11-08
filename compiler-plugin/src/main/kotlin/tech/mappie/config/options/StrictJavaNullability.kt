package tech.mappie.config.options

import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.types.classOrFail
import org.jetbrains.kotlin.ir.util.getValueArgument
import org.jetbrains.kotlin.ir.util.isTrueConst
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.name.Name
import tech.mappie.ir.MappieContext
import tech.mappie.util.CLASS_ID_USE_STRICT_JAVA_NULLABILITY

context(context: MappieContext)
fun useStrictPlatformTypeNullabilityValidationClassSymbol() =
    context.pluginContext.referenceClass(CLASS_ID_USE_STRICT_JAVA_NULLABILITY)

context(context: MappieContext)
fun getUseStrictPlatformTypeNullabilityValidationAnnotation(origin: IrFunction): IrConstructorCall? =
    origin.parentAsClass.annotations.firstOrNull { it.type.classOrFail == useStrictPlatformTypeNullabilityValidationClassSymbol() }

context(context: MappieContext)
fun useStrictPlatformTypeNullabilityValidation(origin: IrFunction): Boolean =
    getUseStrictPlatformTypeNullabilityValidationAnnotation(origin)
        ?.let { it.getValueArgument(Name.identifier("value"))?.isTrueConst() ?: true }
        ?: context.configuration.strictplatformTypeNullability