package tech.mappie.config.options

import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.types.classOrFail
import org.jetbrains.kotlin.ir.util.getValueArgument
import org.jetbrains.kotlin.ir.util.isTrueConst
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
import tech.mappie.MappieContext
import tech.mappie.api.config.UseStrictEnums
import tech.mappie.util.PACKAGE_TECH_MAPPIE_API_CONFIG

fun MappieContext.useStrictEnumsClassSymbol() =
    pluginContext.referenceClass(ClassId(PACKAGE_TECH_MAPPIE_API_CONFIG, Name.identifier(UseStrictEnums::class.simpleName!!)))

fun MappieContext.useStrictEnums(origin: IrFunction): Boolean =
    origin.parentAsClass.annotations
        .firstOrNull { it.type.classOrFail == useStrictEnumsClassSymbol() }
        ?.let { it.getValueArgument(Name.identifier("value"))?.isTrueConst() ?: true }
        ?: configuration.useDefaultArguments