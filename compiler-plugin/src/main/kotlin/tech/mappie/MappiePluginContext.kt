package tech.mappie

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import tech.mappie.api.EnumMappie
import tech.mappie.api.ObjectMappie

interface MappieContext {
    val pluginContext: IrPluginContext
    val reporter: MessageCollector
    val configuration: MappieConfiguration
}


fun MappieContext.referenceObjectMappieClass(): IrClassSymbol =
    pluginContext.referenceClass(ClassId(FqName("tech.mappie.api"), Name.identifier(ObjectMappie::class.simpleName!!)))!!

fun MappieContext.referenceEnumMappieClass(): IrClassSymbol =
    pluginContext.referenceClass(ClassId(FqName("tech.mappie.api"), Name.identifier(EnumMappie::class.simpleName!!)))!!

fun MappieContext.referenceFunctionLet() =
    pluginContext.referenceFunctions(CallableId(FqName("kotlin"), Name.identifier("let"))).first()
