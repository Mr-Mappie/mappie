package tech.mappie.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
import tech.mappie.MappieConfiguration
import tech.mappie.api.EnumMappie
import tech.mappie.api.ObjectMappie
import tech.mappie.util.IDENTIFIER_LET
import tech.mappie.util.KOTLIN_PACKAGE
import tech.mappie.util.MAPPIE_PACKAGE
import tech.mappie.config.MappieConfiguration

interface MappieContext {
    val pluginContext: IrPluginContext
    val logger: MappieLogger
    val configuration: MappieConfiguration
}

fun MappieContext.referenceObjectMappieClass(): IrClassSymbol =
    pluginContext.referenceClass(ClassId(MAPPIE_PACKAGE, Name.identifier(ObjectMappie::class.simpleName!!)))!!

fun MappieContext.referenceEnumMappieClass(): IrClassSymbol =
    pluginContext.referenceClass(ClassId(MAPPIE_PACKAGE, Name.identifier(EnumMappie::class.simpleName!!)))!!

fun MappieContext.referenceFunctionLet() =
    pluginContext.referenceFunctions(CallableId(KOTLIN_PACKAGE, IDENTIFIER_LET)).first()
