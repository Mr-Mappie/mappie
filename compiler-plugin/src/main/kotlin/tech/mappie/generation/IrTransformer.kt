package tech.mappie.generation

import tech.mappie.MappieIrRegistrar.Companion.context
import tech.mappie.resolving.*
import tech.mappie.resolving.classes.*
import tech.mappie.util.*
import tech.mappie.validation.MappingValidation
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.lower.irThrow
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.util.*
import tech.mappie.resolving.enums.ExplicitEnumMappingTarget
import tech.mappie.resolving.enums.ResolvedEnumMappingTarget
import tech.mappie.resolving.enums.ThrowingEnumMappingTarget

class IrTransformer(private val symbols: List<MappieDefinition>) : IrElementTransformerVoidWithContext() {

    override fun visitClassNew(declaration: IrClass): IrStatement {
        declaration.declarations.filterIsInstance<IrClass>().forEach { inner ->
            inner.transform(IrTransformer(symbols), null)
        }

        if (declaration.accept(ShouldTransformCollector(), Unit)) {
            var function = declaration.declarations
                .filterIsInstance<IrSimpleFunction>()
                .first { it.name == IDENTIFIER_MAP }

            if (function.isFakeOverride) {
                declaration.declarations.removeIf { it is IrSimpleFunction && function.name == IDENTIFIER_MAP }
                function = function.realImplementation(declaration)
                declaration.declarations.add(function)
            }

            val transformed = function.transform(this, null)
            if (transformed is IrSimpleFunction && transformed.body == null) {
                declaration.declarations.remove(transformed)
            }
        }
        return declaration
    }

    override fun visitFunctionNew(declaration: IrFunction): IrStatement {
        if (declaration.accept(ShouldTransformCollector(), Unit)) {
            val (valids, invalids) = declaration
                .accept(MappingResolver(), symbols)
                .map { it to MappingValidation.of(declaration.fileEntry, it) }
                .partition { it.second.isValid() }

            if (valids.isNotEmpty()) {
                declaration.body = with(createScope(declaration)) {
                    val (mapping, validation) = MappingSelector.of(valids).select()

                    logAll(validation.warnings())

                    when (mapping) {
                        is ConstructorCallMapping -> {
                            context.blockBody(scope) {
                                +irReturn(irCallConstructor(mapping.symbol, emptyList()).apply {
                                    mapping.mappings.map { (target, source) ->
                                        val index = mapping.symbol.owner.valueParameters.indexOf(target)
                                        putValueArgument(index, source.single().toIr(this@blockBody))
                                    }
                                })
                            }
                        }

                        is EnumMapping -> {
                            context.blockBody(scope) {
                                +irReturn(irWhen(mapping.targetType, mapping.mappings
                                    .filter { (_, targets) -> targets.isNotEmpty() }
                                    .map { (source, targets) ->
                                        val lhs = irGet(declaration.valueParameters.first())
                                        val rhs = irGetEnumValue(mapping.targetType, source.symbol)
                                        val result: IrExpression = when (val target = targets.single()) {
                                            is ExplicitEnumMappingTarget -> irGetEnumValue(mapping.targetType, target.target.symbol)
                                            is ResolvedEnumMappingTarget -> irGetEnumValue(mapping.targetType, target.target.symbol)
                                            is ThrowingEnumMappingTarget -> irThrow(target.exception)
                                        }
                                        irBranch(irEqeqeq(lhs, rhs), result)
                                    } + irElseBranch(irCall(context.irBuiltIns.noWhenBranchMatchedExceptionSymbol))))
                            }
                        }

                        is SingleValueMapping -> {
                            context.blockBody(scope) {
                                +irReturn(mapping.value)
                            }
                        }
                    }
                }
            } else {
                invalids.first().second.problems.forEach { problem ->
                    logError(problem.description, problem.location ?: location(declaration))
                }
            }
        }
        return declaration
    }
}

fun ObjectMappingSource.toIr(builder: IrBuilderWithScope): IrExpression =
    when (this) {
        is ResolvedSource -> toIr(builder)
        is PropertySource -> toIr(builder)
        is ExpressionSource -> toIr(builder)
        is ValueSource -> value
    }

fun ExpressionSource.toIr(builder: IrBuilderWithScope): IrExpression {
    return builder.irCall(context.referenceLetFunction()).apply {
        extensionReceiver = builder.irGet(type, extensionReceiverSymbol)
        putValueArgument(0, expression)
    }
}

fun ResolvedSource.toIr(builder: IrBuilderWithScope): IrExpression {
    val getter = builder.irCall(property).apply {
        dispatchReceiver = this@toIr.dispatchReceiver
    }
    return via?.let {
        builder.irCall(via).apply {
            dispatchReceiver = viaDispatchReceiver
            putValueArgument(0, getter)
        }
    } ?: getter
}

fun PropertySource.toIr(builder: IrBuilderWithScope): IrExpression {
    val getter = builder.irCall(property).apply {
        dispatchReceiver = this@toIr.dispatchReceiver
    }
    return transformation?.let {
        builder.irCall(context.referenceLetFunction()).apply {
            extensionReceiver = getter
            putValueArgument(0, transformation)
    } } ?: getter
}

