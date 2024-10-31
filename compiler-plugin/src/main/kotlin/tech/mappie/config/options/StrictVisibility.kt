package tech.mappie.config.options

import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.types.classOrFail
import org.jetbrains.kotlin.ir.util.getValueArgument
import org.jetbrains.kotlin.ir.util.isTrueConst
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
import tech.mappie.MappieContext
import tech.mappie.api.config.UseStrictVisibility
import tech.mappie.util.PACKAGE_TECH_MAPPIE_API_CONFIG

fun MappieContext.useStrictVisibilityClassSymbol() =
    pluginContext.referenceClass(ClassId(PACKAGE_TECH_MAPPIE_API_CONFIG, Name.identifier(UseStrictVisibility::class.simpleName!!)))

fun MappieContext.getUseStrictVisibilityAnnotation(origin: IrFunction): IrConstructorCall? = origin.parentAsClass.annotations
    .firstOrNull { it.type.classOrFail == useStrictVisibilityClassSymbol() }

fun MappieContext.useStrictVisibility(origin: IrFunction): Boolean =
    getUseStrictVisibilityAnnotation(origin)
        ?.let { it.getValueArgument(Name.identifier("value"))?.isTrueConst() ?: true }
        ?: configuration.strictness.visibility