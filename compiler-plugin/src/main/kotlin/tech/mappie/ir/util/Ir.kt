package tech.mappie.ir.util

import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.ir.builders.IrBlockBodyBuilder
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.IrGeneratorContextBase
import org.jetbrains.kotlin.ir.builders.IrGeneratorContextInterface
import org.jetbrains.kotlin.ir.builders.Scope
import org.jetbrains.kotlin.ir.builders.declarations.buildFun
import org.jetbrains.kotlin.ir.builders.irBlockBody
import org.jetbrains.kotlin.ir.builders.parent
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.expressions.IrStatementOrigin
import org.jetbrains.kotlin.ir.expressions.impl.IrFunctionExpressionImpl
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.defaultType
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.SYNTHETIC_OFFSET
import org.jetbrains.kotlin.ir.util.isNullable
import org.jetbrains.kotlin.ir.util.erasedUpperBound
import org.jetbrains.kotlin.ir.util.isStrictSubtypeOfClass
import org.jetbrains.kotlin.name.Name
import tech.mappie.MappieContext
import tech.mappie.referenceMappieClass
import tech.mappie.util.*

context (context: MappieContext)
fun IrClass.mappieSuperType(): IrType? =
    allSuperTypes().singleOrNull { it.isStrictSubtypeOfClass(context.referenceMappieClass()) }

fun IrClass.isSubclassOf(clazz: IrClassSymbol) =
    allSuperTypes().any { it.erasedUpperBound == clazz.defaultType.getClass()!! }

fun IrClass.allSuperTypes(): List<IrType> =
    superTypes + superTypes.flatMap { it.erasedUpperBound.allSuperTypes() }

fun getterName(name: String) =
    Name.special("<get-$name>")

fun IrSimpleFunction.isMappieMapFunction() =
    name == IDENTIFIER_MAP && overriddenSymbols.isNotEmpty()

fun IrSimpleFunction.isMappieMapNullableListFunction() =
    name == IDENTIFIER_MAP_NULLABLE_LIST
            && parameters.singleOrNull { it.kind == IrParameterKind.Regular }?.type?.isList() == true
            && returnType.isList()

fun IrSimpleFunction.isMappieMapListFunction() =
    name == IDENTIFIER_MAP_LIST
        && parameters.singleOrNull { it.kind == IrParameterKind.Regular }?.type?.isList() == true
        && returnType.isList()

fun IrSimpleFunction.isMappieMapNullableSetFunction() =
    name == IDENTIFIER_MAP_NULLABLE_SET
        && parameters.singleOrNull { it.kind == IrParameterKind.Regular }?.type?.isSet() == true
        && returnType.isSet()

fun IrSimpleFunction.isMappieMapSetFunction() =
    name == IDENTIFIER_MAP_SET
        && parameters.singleOrNull { it.kind == IrParameterKind.Regular }?.type?.isSet() == true
        && returnType.isSet()

fun IrSimpleFunction.isMappieMapNullableFunction() =
    name == IDENTIFIER_MAP_NULLABLE
        && parameters.singleOrNull { it.kind == IrParameterKind.Regular }?.type?.isNullable() == true
        && returnType.isNullable()

fun IrGeneratorContextInterface.blockBody(scope: Scope, body: IrBlockBodyBuilder.() -> Unit) =
    IrBlockBodyBuilder(
        IrGeneratorContextBase(irBuiltIns),
        scope,
        scope.scopeOwnerSymbol.owner.startOffset,
        scope.scopeOwnerSymbol.owner.endOffset,
    ).blockBody(body)

fun IrBuilderWithScope.irLambda(
    returnType: IrType,
    lambdaType: IrType,
    block: IrBlockBodyBuilder.() -> Unit,
): IrFunctionExpression {
    val scope = this
    val lambda = context.irFactory.buildFun {
        startOffset = SYNTHETIC_OFFSET
        endOffset = SYNTHETIC_OFFSET
        name = Name.special("<anonymous>")
        this.returnType = returnType
        visibility = DescriptorVisibilities.LOCAL
        origin = IrDeclarationOrigin.LOCAL_FUNCTION_FOR_LAMBDA
    }.apply {
        val bodyBuilder = DeclarationIrBuilder(context, symbol, startOffset, endOffset)
        body = bodyBuilder.irBlockBody {
            block()
        }
        parent = scope.parent
    }
    return IrFunctionExpressionImpl(startOffset, endOffset, lambdaType, lambda, IrStatementOrigin.LAMBDA)
}
