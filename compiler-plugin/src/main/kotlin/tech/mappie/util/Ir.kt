package tech.mappie.util

import org.jetbrains.kotlin.backend.jvm.ir.erasedUpperBound
import org.jetbrains.kotlin.ir.builders.IrBlockBodyBuilder
import org.jetbrains.kotlin.ir.builders.IrGeneratorContextBase
import org.jetbrains.kotlin.ir.builders.IrGeneratorContextInterface
import org.jetbrains.kotlin.ir.builders.Scope
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.Name
import kotlin.reflect.KClass

fun IrClass.isSubclassOf(clazz: KClass<*>) =
    allSuperTypes().any { it.erasedUpperBound.fqNameWhenAvailable?.asString() == clazz.java.name }

fun IrClass.allSuperTypes(): List<IrType> =
    superTypes + superTypes.flatMap { it.erasedUpperBound.allSuperTypes() }

fun getterName(name: String) =
    Name.special("<get-$name>")

fun IrSimpleFunction.isMappieMapFunction() =
    name == IDENTIFIER_MAP && overriddenSymbols.isNotEmpty()

fun IrSimpleFunction.isMappieMapNullableListFunction() =
    name == IDENTIFIER_MAP_NULLABLE_LIST
            && valueParameters.singleOrNull()?.type?.isList() == true
            && returnType.isList()

fun IrSimpleFunction.isMappieMapListFunction() =
    name == IDENTIFIER_MAP_LIST
        && valueParameters.singleOrNull()?.type?.isList() == true
        && returnType.isList()

fun IrSimpleFunction.isMappieMapNullableSetFunction() =
    name == IDENTIFIER_MAP_NULLABLE_SET
            && valueParameters.singleOrNull()?.type?.isSet() == true
            && returnType.isSet()

fun IrSimpleFunction.isMappieMapSetFunction() =
    name == IDENTIFIER_MAP_SET
        && valueParameters.singleOrNull()?.type?.isSet() == true
        && returnType.isSet()

fun IrSimpleFunction.isMappieMapNullableFunction() =
    name == IDENTIFIER_MAP_NULLABLE
        && valueParameters.singleOrNull()?.type?.isNullable() == true
        && returnType.isNullable()

fun IrGeneratorContextInterface.blockBody(scope: Scope, body: IrBlockBodyBuilder.() -> Unit) =
    IrBlockBodyBuilder(
        IrGeneratorContextBase(irBuiltIns),
        scope,
        scope.scopeOwnerSymbol.owner.startOffset,
        scope.scopeOwnerSymbol.owner.endOffset,
    ).blockBody(body)
