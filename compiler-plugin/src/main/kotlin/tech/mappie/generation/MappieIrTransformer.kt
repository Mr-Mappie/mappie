package tech.mappie.generation

import tech.mappie.MappieIrRegistrar.Companion.context
import tech.mappie.resolving.*
import tech.mappie.resolving.classes.*
import tech.mappie.util.*
import tech.mappie.validation.MappingValidation
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.lower.irThrow
import org.jetbrains.kotlin.ir.IrFileEntry
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.expressions.IrPropertyReference
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classOrFail
import org.jetbrains.kotlin.ir.types.typeOrFail
import org.jetbrains.kotlin.ir.util.*
import tech.mappie.MappieIrRegistrar
import tech.mappie.resolving.enums.ExplicitEnumMappingTarget
import tech.mappie.resolving.enums.ResolvedEnumMappingTarget
import tech.mappie.resolving.enums.ThrowingEnumMappingTarget

class MappieIrTransformer(private val symbols: List<MappieDefinition>) : IrElementTransformerVoidWithContext() {

    override fun visitClassNew(declaration: IrClass): IrStatement {
        declaration.declarations.filterIsInstance<IrClass>().forEach { inner ->
            inner.transform(MappieIrTransformer(symbols), null)
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

                    logAll(validation.warnings(), location(declaration))

                    when (mapping) {
                        is ConstructorCallMapping -> {
                            context.blockBody(scope) {
                                +irReturn(irCallConstructor(mapping.symbol, emptyList()).apply {
                                    mapping.mappings.map { (target, source) ->
                                        val file = declaration.fileEntry
                                        val index = mapping.symbol.owner.valueParameters.indexOf(target)
                                        putValueArgument(index, generateValueArgument(file, source.single(), declaration.valueParameters))
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
                val first = invalids.firstOrNull()
                if (first != null) {
                    logAll(invalids.first().second.problems, location(declaration))
                } else {
                    logError("No constructor visible to use", location(declaration))
                }
            }
        }
        return declaration
    }
}

fun IrBuilderWithScope.generateValueArgument(file: IrFileEntry, source: ObjectMappingSource, parameters: List<IrValueParameter>): IrExpression {
    return when (source) {
        is ResolvedSource -> generateResolvedValueArgument(source)
        is PropertySource -> generatePropertyValueArgument(file, source, parameters)
        is ExpressionSource -> generateExpressionValueArgument(source, parameters)
        is ValueSource -> source.value
    }
}

fun IrBuilderWithScope.generateResolvedValueArgument(source: ResolvedSource): IrFunctionAccessExpression {
    val getter = irCall(source.property.function).apply {
        dispatchReceiver = irGet(source.property.holder)
    }
    return source.via?.let {
        irCall(source.via).apply {
            dispatchReceiver = source.viaDispatchReceiver
            putValueArgument(0, getter)
        }
    } ?: getter
}

fun IrBuilderWithScope.generatePropertyValueArgument(file: IrFileEntry, source: PropertySource, parameters: List<IrValueParameter>): IrFunctionAccessExpression {
    val getter = irCall(source.getter).apply {
        dispatchReceiver = source.property.dispatchReceiver
            ?: irGet(parameters.singleOrNull { it.type == source.property.targetType } ?: run {
                logError("Could not determine value parameters for property reference. Please use a property reference of an object instead of the class", location(file, source.property))
                error("Mappie resolving failed")
            })
    }
    return source.transformation?.let {
        irCall(MappieIrRegistrar.context.referenceLetFunction()).apply {
            extensionReceiver = getter
            putValueArgument(0, source.transformation)
    } } ?: getter
}

fun IrBuilderWithScope.generateExpressionValueArgument(source: ExpressionSource, parameters: List<IrValueParameter>): IrFunctionAccessExpression {
    return irCall(MappieIrRegistrar.context.referenceLetFunction()).apply {
        extensionReceiver = irGet(parameters.single())
        putValueArgument(0, source.expression)
    }
}