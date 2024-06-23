package tech.mappie.generation

import tech.mappie.MappieIrRegistrar.Companion.context
import tech.mappie.resolving.*
import tech.mappie.resolving.classes.*
import tech.mappie.util.*
import tech.mappie.validation.MappingValidation
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.util.*

class IrTransformer : IrElementTransformerVoidWithContext() {

    override fun visitClassNew(declaration: IrClass): IrStatement {
        if (declaration.accept(ShouldTransformCollector(), Unit)) {
            var function = declaration.declarations
                .filterIsInstance<IrSimpleFunction>()
                .first { it.name == IDENTIFIER_MAP }

            if (function.isFakeOverride) {
                declaration.declarations.removeIf { it is IrSimpleFunction && function.name == IDENTIFIER_MAP }
                function = function.realImplementation(declaration)
                declaration.declarations.add(function)
            }

            val transformed = function.accept(this, null)
            if (transformed is IrSimpleFunction && transformed.body == null) {
                declaration.declarations.remove(transformed)
            }
        }
        return declaration
    }

    override fun visitFunctionNew(declaration: IrFunction): IrStatement {
        if (declaration.accept(ShouldTransformCollector(), Unit)) {
            val (valids, invalids) = declaration.accept(MappingResolver(), Unit)
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
                                        irBranch(
                                            irEqeqeq(lhs, rhs),
                                            irGetEnumValue(mapping.targetType, targets.single().symbol)
                                        )
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
        is PropertySource -> toIr(builder)
        is DefaultParameterValueSource -> value
        is ExpressionSource -> toIr(builder)
        is ConstantSource<*> -> value
    }

fun ExpressionSource.toIr(builder: IrBuilderWithScope): IrExpression {
    return builder.irCall(context.referenceLetFunction()).apply {
        extensionReceiver = builder.irGet(type, extensionReceiverSymbol)
        putValueArgument(0, expression)
    }
}

fun PropertySource.toIr(builder: IrBuilderWithScope): IrExpression {
    val getter = builder.irCall(property).apply { dispatchReceiver = builder.irGet(type, dispatchReceiverSymbol) }
    return transformation?.let {
            builder.irCall(context.referenceLetFunction()).apply {
                extensionReceiver = getter
                putValueArgument(0, transformation)
        }
    } ?: getter
}

