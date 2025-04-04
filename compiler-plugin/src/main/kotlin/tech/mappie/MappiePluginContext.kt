package tech.mappie

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.util.superClass
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

fun MappieContext.allMappieClasses(): Set<IrClassSymbol> = setOf(
    referenceEnumMappieClass(),
    referenceObjectMappieClass(),
    referenceObjectMappieClass2(),
    referenceObjectMappieClass3(),
    referenceObjectMappieClass4(),
    referenceObjectMappieClass5(),
)

fun MappieContext.referenceMappieClass(): IrClassSymbol =
    pluginContext.referenceClass(CLASS_ID_MAPPIE)!!

fun MappieContext.referenceObjectMappieClass(): IrClassSymbol =
    pluginContext.referenceClass(CLASS_ID_OBJECT_MAPPIE)!!

fun MappieContext.referenceObjectMappieClass2(): IrClassSymbol =
    pluginContext.referenceClass(CLASS_ID_OBJECT_MAPPIE2)!!

fun MappieContext.referenceObjectMappieClass3(): IrClassSymbol =
    pluginContext.referenceClass(CLASS_ID_OBJECT_MAPPIE3)!!

fun MappieContext.referenceObjectMappieClass4(): IrClassSymbol =
    pluginContext.referenceClass(CLASS_ID_OBJECT_MAPPIE4)!!

fun MappieContext.referenceObjectMappieClass5(): IrClassSymbol =
    pluginContext.referenceClass(CLASS_ID_OBJECT_MAPPIE5)!!

fun MappieContext.referenceEnumMappieClass(): IrClassSymbol =
    pluginContext.referenceClass(CLASS_ID_ENUM_MAPPIE)!!

fun MappieContext.referenceFunctionLet() =
    pluginContext.referenceFunctions(CallableId(PACKAGE_KOTLIN, Name.identifier("let"))).first()

fun MappieContext.referenceFunctionRequireNotNull() =
    pluginContext.referenceFunctions(CallableId(PACKAGE_KOTLIN, Name.identifier("requireNotNull"))).first()

fun MappieContext.shouldGenerateCode(clazz: IrClass) =
    clazz.superClass?.symbol in allMappieClasses()