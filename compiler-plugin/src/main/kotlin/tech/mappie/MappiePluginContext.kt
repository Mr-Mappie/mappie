package tech.mappie

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
import tech.mappie.api.EnumMappie
import tech.mappie.api.ObjectMappie
import tech.mappie.config.MappieConfiguration
import tech.mappie.ir.MappieLogger
import tech.mappie.util.PACKAGE_KOTLIN
import tech.mappie.util.PACKAGE_TECH_MAPPIE_API

interface MappieContext {
    val pluginContext: IrPluginContext
    val logger: MappieLogger
    val configuration: MappieConfiguration
}

fun MappieContext.referenceObjectMappieClass(): IrClassSymbol =
    pluginContext.referenceClass(ClassId(PACKAGE_TECH_MAPPIE_API, Name.identifier(ObjectMappie::class.simpleName!!)))!!

fun MappieContext.referenceEnumMappieClass(): IrClassSymbol =
    pluginContext.referenceClass(ClassId(PACKAGE_TECH_MAPPIE_API, Name.identifier(EnumMappie::class.simpleName!!)))!!

fun MappieContext.referenceFunctionLet() =
    pluginContext.referenceFunctions(CallableId(PACKAGE_KOTLIN, Name.identifier("let"))).first()

fun MappieContext.referenceFunctionRequireNotNull() =
    pluginContext.referenceFunctions(CallableId(PACKAGE_KOTLIN, Name.identifier("requireNotNull"))).first()