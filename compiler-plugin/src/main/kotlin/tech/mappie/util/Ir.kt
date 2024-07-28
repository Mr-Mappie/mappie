package tech.mappie.util

import tech.mappie.MappieIrRegistrar.Companion.context
import tech.mappie.MappiePluginContext
import org.jetbrains.kotlin.backend.jvm.ir.erasedUpperBound
import org.jetbrains.kotlin.ir.IrFileEntry
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrPropertyReference
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.StandardClassIds.Annotations.FlexibleNullability
import tech.mappie.api.EnumMappie
import tech.mappie.mappieTerminate
import tech.mappie.resolving.IDENTIFIER_MAP
import tech.mappie.resolving.IDENTIFIER_MAP_LIST
import tech.mappie.resolving.IDENTIFIER_MAP_NULLABLE
import tech.mappie.resolving.IDENTIFIER_MAP_SET
import kotlin.reflect.KClass

internal fun IrClass.isStrictSubclassOf(vararg classes: KClass<*>): Boolean =
    allSuperTypes().any { classes.any { clazz -> it.erasedUpperBound.fqNameWhenAvailable?.asString() == clazz.java.name } }

internal fun IrClass.isStrictSubclassOf(clazz: KClass<*>): Boolean =
    allSuperTypes().any { it.erasedUpperBound.fqNameWhenAvailable?.asString() == clazz.java.name }

internal fun IrClass.allSuperTypes(): List<IrType> =
    superTypes + superTypes.flatMap { it.erasedUpperBound.allSuperTypes() }

fun IrType.isAssignableFrom(other: IrType, ignoreFlexibleNullability: Boolean = false): Boolean {
    val other = if (ignoreFlexibleNullability && other.isFlexibleNullable()) other.makeNotNull() else other
    return other.isSubtypeOf(this, IrTypeSystemContextImpl(context.irBuiltIns)) || isIntegerAssignableFrom(other)
}

fun IrType.isFlexibleNullable(): Boolean =
    annotations.any { it.symbol.owner.parentAsClass.classId == FlexibleNullability }

fun IrPropertyReference.targetType(file: IrFileEntry): IrType =
    when (type.classOrFail) {
        context.irBuiltIns.kProperty0Class -> dispatchReceiver!!.type
        context.irBuiltIns.kProperty1Class -> (this.type as IrSimpleType).arguments.first().typeOrFail
        else -> mappieTerminate("Unknown KProperty ${dumpKotlinLike()}", location(file, this))
    }

fun IrType.isIntegerAssignableFrom(other: IrType): Boolean =
    when (makeNullable()) {
        context.irBuiltIns.byteType.makeNullable() ->
            other in listOf(context.irBuiltIns.byteType)
        context.irBuiltIns.shortType.makeNullable() ->
            other in listOf(context.irBuiltIns.byteType, context.irBuiltIns.shortType)
        context.irBuiltIns.intType.makeNullable() ->
            other in listOf(context.irBuiltIns.byteType, context.irBuiltIns.shortType, context.irBuiltIns.intType)
        context.irBuiltIns.longType.makeNullable() ->
            other in listOf(context.irBuiltIns.byteType, context.irBuiltIns.shortType, context.irBuiltIns.intType, context.irBuiltIns.longType)
        else -> false
    }

fun IrType.isList() =
    classOrNull?.owner?.fqNameWhenAvailable?.asString() == List::class.qualifiedName

fun IrType.isSet() =
    classOrNull?.owner?.fqNameWhenAvailable?.asString() == Set::class.qualifiedName

fun getterName(name: Name) =
    getterName(name.asString())

fun getterName(name: String) =
    Name.special("<get-$name>")

fun MappiePluginContext.referenceFunctionLet() =
    referenceFunctions(CallableId(FqName("kotlin"), Name.identifier("let"))).first()

fun IrEnumEntry.referenceFunctionValueOf(): IrSimpleFunction =
    parentAsClass.declarations
        .filterIsInstance<IrSimpleFunction>()
        .single { it.name == Name.identifier("valueOf") }

fun referenceEnumMappieClass(): IrClassSymbol =
    context.referenceClass(ClassId(FqName("tech.mappie.api"), Name.identifier(EnumMappie::class.simpleName!!)))!!

fun IrSimpleFunctionSymbol.dumpKotlinLike(): String =
    owner.run {
        parentAsClass.name.asString() + "::" + name.asString().removeSurrounding("<", ">").removePrefix("get-")
    }

fun IrSimpleFunction.isMappieMapFunction() =
    name == IDENTIFIER_MAP && overriddenSymbols.isNotEmpty()

fun IrSimpleFunction.isMappieMapListFunction() =
    name == IDENTIFIER_MAP_LIST
        && valueParameters.singleOrNull()?.type?.isList() == true
        && returnType.isList()

fun IrSimpleFunction.isMappieMapSetFunction() =
    name == IDENTIFIER_MAP_SET
        && valueParameters.singleOrNull()?.type?.isSet() == true
        && returnType.isSet()

fun IrSimpleFunction.isMappieMapNullableFunction() =
    name == IDENTIFIER_MAP_NULLABLE
        && valueParameters.singleOrNull()?.type?.isNullable() == true
        && returnType.isNullable()