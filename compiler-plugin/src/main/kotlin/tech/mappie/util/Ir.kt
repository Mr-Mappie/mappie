package tech.mappie.util

import tech.mappie.MappieIrRegistrar.Companion.context
import tech.mappie.MappiePluginContext
import org.jetbrains.kotlin.backend.jvm.ir.erasedUpperBound
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrGetEnumValue
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.expressions.impl.*
import org.jetbrains.kotlin.ir.symbols.IrEnumEntrySymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.IrValueSymbol
import org.jetbrains.kotlin.ir.symbols.impl.IrSimpleFunctionSymbolImpl
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.StandardClassIds.Annotations.FlexibleNullability
import tech.mappie.resolving.IDENTIFIER_MAP
import kotlin.reflect.KClass

internal fun IrClass.isStrictSubclassOf(clazz: KClass<*>): Boolean =
    allSuperTypes().any { it.erasedUpperBound.fqNameWhenAvailable?.asString() == clazz.java.name }

internal fun IrClass.allSuperTypes(): List<IrType> =
    superTypes + superTypes.flatMap { it.erasedUpperBound.allSuperTypes() }

fun IrType.isAssignableFrom(other: IrType, ignoreFlexibleNullability: Boolean = false): Boolean {
    val other = if (ignoreFlexibleNullability && other.isFlexibleNullable()) other.makeNotNull() else other
    return other.isSubtypeOf(this, IrTypeSystemContextImpl(context.irBuiltIns)) || isIntegerAssignableFrom(other)
}

fun IrType.isFlexibleNullable(): Boolean =
    hasAnnotation(FlexibleNullability)

fun IrType.isIntegerAssignableFrom(other: IrType): Boolean =
    when (this) {
        context.irBuiltIns.byteType -> other in listOf(context.irBuiltIns.byteType)
        context.irBuiltIns.shortType -> other in listOf(context.irBuiltIns.byteType, context.irBuiltIns.shortType)
        context.irBuiltIns.intType -> other in listOf(context.irBuiltIns.byteType, context.irBuiltIns.shortType, context.irBuiltIns.intType)
        context.irBuiltIns.longType -> other in listOf(context.irBuiltIns.byteType, context.irBuiltIns.shortType, context.irBuiltIns.intType, context.irBuiltIns.longType)
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

fun MappiePluginContext.referenceLetFunction() =
    referenceFunctions(CallableId(FqName("kotlin"), Name.identifier("let"))).first()

fun irGetEnumValue(type: IrType, symbol: IrEnumEntrySymbol): IrGetEnumValue =
    IrGetEnumValueImpl(SYNTHETIC_OFFSET, SYNTHETIC_OFFSET, type, symbol)

fun irGet(declaration: IrValueDeclaration) =
    irGet(declaration.type, declaration.symbol)

fun irGet(type: IrType, symbol: IrValueSymbol): IrGetValue =
    IrGetValueImpl(SYNTHETIC_OFFSET, SYNTHETIC_OFFSET, type, symbol)

fun irConstructorCall(constructor: IrConstructor) =
    IrConstructorCallImpl(
        SYNTHETIC_OFFSET,
        SYNTHETIC_OFFSET,
        constructor.returnType,
        constructor.symbol,
        0,
        0,
        0,
    )

fun IrSimpleFunction.realImplementation(parent: IrDeclarationParent) =
    factory.createSimpleFunction(
        parent.startOffset,
        parent.endOffset,
        IrDeclarationOrigin.DEFINED,
        name,
        visibility,
        isInline,
        isExpect,
        returnType,
        modality,
        IrSimpleFunctionSymbolImpl(),
        false,
        isSuspend,
        isOperator,
        isInfix,
        isExternal,
        isFakeOverride = false
    ).also {
        val parentClass = parentAsClass.superClass!!
        val parentFunction = parentClass.getSimpleFunction(IDENTIFIER_MAP.asString())!!
        it.parent = parent
        it.overriddenSymbols = listOf(parentFunction.owner.symbol)
        it.dispatchReceiverParameter = dispatchReceiverParameter
        valueParameters.forEach { valueParameter ->
            it.addValueParameter(valueParameter.name, valueParameter.type)
        }
    }


fun IrSimpleFunctionSymbol.dumpKotlinLike(): String =
    owner.run {
        parentAsClass.name.asString() + "::" + name.asString().removeSurrounding("<", ">").removePrefix("get-")
    }
