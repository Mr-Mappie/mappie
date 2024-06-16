package io.github.mappie.generation

import io.github.mappie.MappieIrRegistrar.Companion.context
import io.github.mappie.resolving.classes.ConstantSource
import io.github.mappie.resolving.classes.MappingSource
import io.github.mappie.resolving.classes.PropertySource
import io.github.mappie.resolving.*
import io.github.mappie.resolving.classes.DefaultParameterValueSource
import io.github.mappie.util.*
import io.github.mappie.validation.MappingValidation
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.util.*

class IrTransformer : IrElementTransformerVoidWithContext() {

    override fun visitFileNew(declaration: IrFile): IrFile {
        val result = super.visitFileNew(declaration)
        return result
    }

    override fun visitClassNew(declaration: IrClass): IrStatement {
        if (declaration.accept(ShouldTransformCollector(), Unit)) {
            return super.visitClassNew(declaration)
        }
        return declaration
    }

    override fun visitFunctionNew(declaration: IrFunction): IrStatement {
        if (declaration.accept(ShouldTransformCollector(), Unit)) {
            val (valids, invalids) = declaration.accept(MappingResolver(), Unit)
                .map { it to MappingValidation.of(declaration.fileEntry, it)  }
                .partition { it.second.isValid() }

            if (valids.isNotEmpty()) {
                declaration.body = with(createScope(declaration)) {
                    val (mapping, validation) = MappingSelector.of(valids).select()
                    validation.warnings().forEach { warning ->
                        context.messageCollector.warn(warning.description, warning.location)
                    }
                    when (mapping) {
                        is ConstructorCallMapping -> {
                            context.blockBody(this.scope) {
                                +irReturn(irCallConstructor(mapping.symbol, emptyList()).apply {
                                    mapping.mappings.map { (target, source) ->
                                        val index = mapping.symbol.owner.valueParameters.indexOf(target)
                                        putValueArgument(index, source.single().toIr(this@blockBody))
                                    }
                                })
                            }
                        }
                        is EnumMapping -> {
                            context.blockBody(this.scope) {
                                +irReturn(irWhen(mapping.targetType, mapping.mappings
                                    .filter { (_, targets) -> targets.isNotEmpty() }
                                    .map { (source, targets) ->
                                    val lhs = irGet(declaration.valueParameters.first())
                                    val rhs = irGetEnumValue(mapping.targetType, source.symbol)
                                    irBranch(irEqeqeq(lhs, rhs), irGetEnumValue(mapping.targetType, targets.single().symbol))
                                } + irElseBranch(irCall(context.irBuiltIns.noWhenBranchMatchedExceptionSymbol))))
                            }
                        }
                        is SingleValueMapping -> {
                            context.blockBody(this.scope) {
                                +irReturn(mapping.value)
                            }
                        }
                    }
                }
            } else {
                invalids.first().second.problems.forEach { problem ->
                    context.messageCollector.error(problem.description, problem.location ?: location(declaration))
                }
            }
        }
        return declaration
    }
}

fun MappingSource.toIr(builder: IrBuilderWithScope): IrExpression =
    when (this) {
        is PropertySource -> toIr(builder)
        is DefaultParameterValueSource -> value
        is ConstantSource<*> -> value
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

