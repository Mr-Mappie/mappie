package tech.mappie.ir

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.util.hasShape
import org.jetbrains.kotlin.ir.util.superClass
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name.identifier
import tech.mappie.config.MappieConfiguration
import tech.mappie.ir.resolving.MappieDefinitionCollection
import tech.mappie.util.*

interface MappieContext {
    val pluginContext: IrPluginContext
    val logger: MappieLogger
    val configuration: MappieConfiguration
    val definitions: MappieDefinitionCollection
}

context(context: MappieContext)
fun allMappieClasses(): Set<IrClassSymbol> = setOf(
    referenceEnumMappieClass(),
    referenceObjectMappieClass(),
    referenceObjectMappieClass2(),
    referenceObjectMappieClass3(),
    referenceObjectMappieClass4(),
    referenceObjectMappieClass5(),
)

context(context: MappieContext)
fun referenceMappieClass(): IrClassSymbol =
    context.pluginContext.referenceClass(CLASS_ID_MAPPIE)!!

context(context: MappieContext)
fun referenceObjectMappieClass(): IrClassSymbol =
    context.pluginContext.referenceClass(CLASS_ID_OBJECT_MAPPIE)!!

context(context: MappieContext)
fun referenceObjectMappieClass2(): IrClassSymbol =
    context.pluginContext.referenceClass(CLASS_ID_OBJECT_MAPPIE2)!!

context(context: MappieContext)
fun referenceObjectMappieClass3(): IrClassSymbol =
    context.pluginContext.referenceClass(CLASS_ID_OBJECT_MAPPIE3)!!

context(context: MappieContext)
fun referenceObjectMappieClass4(): IrClassSymbol =
    context.pluginContext.referenceClass(CLASS_ID_OBJECT_MAPPIE4)!!

context(context: MappieContext)
fun referenceObjectMappieClass5(): IrClassSymbol =
    context.pluginContext.referenceClass(CLASS_ID_OBJECT_MAPPIE5)!!

context(context: MappieContext)
fun referenceEnumMappieClass(): IrClassSymbol =
    context.pluginContext.referenceClass(CLASS_ID_ENUM_MAPPIE)!!

context(context: MappieContext)
fun referenceFunctionLet() =
    context.pluginContext.referenceFunctions(CallableId(PACKAGE_KOTLIN, identifier("let"))).first()

context(context: MappieContext)
fun referenceFunctionRequireNotNull() =
    context.pluginContext.referenceFunctions(CallableId(PACKAGE_KOTLIN, identifier("requireNotNull"))).first {
        it.owner.hasShape(regularParameters = 2)
    }

context(context: MappieContext)
fun referenceFunctionRun() =
    context.pluginContext.referenceFunctions(CallableId(PACKAGE_KOTLIN, identifier("run"))).first {
        it.owner.hasShape(regularParameters = 1)
    }

context(context: MappieContext)
fun referenceFunctionError() =
    context.pluginContext.referenceFunctions(CallableId(PACKAGE_KOTLIN, identifier("error"))).first()

context(context: MappieContext)
fun shouldGenerateCode(clazz: IrClass) =
    clazz.superClass?.symbol in allMappieClasses()