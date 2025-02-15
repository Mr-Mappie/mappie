package tech.mappie

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name
import tech.mappie.config.MappieConfiguration
import tech.mappie.ir.MappieLogger
import tech.mappie.util.*

interface MappieContext {
    val pluginContext: IrPluginContext
    val logger: MappieLogger
    val configuration: MappieConfiguration
}

fun MappieContext.referenceMappieClass(): IrClassSymbol =
    pluginContext.referenceClass(CLASS_ID_MAPPIE)!!

fun MappieContext.referenceObjectMappieClass(): IrClassSymbol =
    pluginContext.referenceClass(CLASS_ID_OBJECT_MAPPIE)!!

fun MappieContext.referenceEnumMappieClass(): IrClassSymbol =
    pluginContext.referenceClass(CLASS_ID_ENUM_MAPPIE)!!

fun MappieContext.referenceFunctionLet() =
    pluginContext.referenceFunctions(CallableId(PACKAGE_KOTLIN, Name.identifier("let"))).first()

fun MappieContext.referenceFunctionRequireNotNull() =
    pluginContext.referenceFunctions(CallableId(PACKAGE_KOTLIN, Name.identifier("requireNotNull"))).first()