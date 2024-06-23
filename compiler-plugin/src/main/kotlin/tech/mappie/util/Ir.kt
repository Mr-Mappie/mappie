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
import org.jetbrains.kotlin.ir.symbols.IrValueSymbol
import org.jetbrains.kotlin.ir.symbols.impl.IrSimpleFunctionSymbolImpl
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import tech.mappie.resolving.IDENTIFIER_MAP

internal fun IrClass.isSubclassOfFqName(fqName: String): Boolean =
    allSuperTypes().any { it.erasedUpperBound.fqNameWhenAvailable?.asString() == fqName }

internal fun IrClass.allSuperTypes(): List<IrType> =
    this.superTypes + this.superTypes.flatMap { it.erasedUpperBound.allSuperTypes() }

fun IrType.isAssignableFrom(other: IrType): Boolean =
    isSubtypeOf(other, IrTypeSystemContextImpl(context.irBuiltIns)) && (isNullable() || !other.isNullable())

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