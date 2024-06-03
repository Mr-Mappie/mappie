package io.github.mappie.util

import io.github.mappie.MappiePluginContext
import org.jetbrains.kotlin.backend.jvm.ir.erasedUpperBound
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrValueDeclaration
import org.jetbrains.kotlin.ir.expressions.IrGetEnumValue
import org.jetbrains.kotlin.ir.expressions.IrGetValue
import org.jetbrains.kotlin.ir.expressions.impl.IrGetEnumValueImpl
import org.jetbrains.kotlin.ir.expressions.impl.IrGetValueImpl
import org.jetbrains.kotlin.ir.symbols.IrEnumEntrySymbol
import org.jetbrains.kotlin.ir.symbols.IrValueSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.SYNTHETIC_OFFSET
import org.jetbrains.kotlin.ir.util.fqNameWhenAvailable
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

internal fun IrClass.isSubclassOfFqName(fqName: String): Boolean =
    fqNameWhenAvailable?.asString() == fqName || superTypes.any { it.erasedUpperBound.isSubclassOfFqName(fqName) }

fun MappiePluginContext.referenceLetFunction() =
    referenceFunctions(CallableId(FqName("kotlin"), Name.identifier("let"))).first()

fun irGetEnumValue(type: IrType, symbol: IrEnumEntrySymbol): IrGetEnumValue =
    IrGetEnumValueImpl(SYNTHETIC_OFFSET, SYNTHETIC_OFFSET, type, symbol)

fun irGet(declaration: IrValueDeclaration) =
    irGet(declaration.type, declaration.symbol)

fun irGet(type: IrType, symbol: IrValueSymbol): IrGetValue =
    IrGetValueImpl(SYNTHETIC_OFFSET, SYNTHETIC_OFFSET, type, symbol)