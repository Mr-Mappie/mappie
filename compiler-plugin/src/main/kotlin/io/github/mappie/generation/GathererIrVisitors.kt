package io.github.mappie.generation

import io.github.mappie.MappieIrRegistrar.Companion.context
import io.github.mappie.resolving.classes.ConstantSource
import io.github.mappie.resolving.classes.MappingSource
import io.github.mappie.resolving.classes.PropertySource
import io.github.mappie.resolving.*
import io.github.mappie.util.*
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.*

@OptIn(UnsafeDuringIrConstructionAPI::class)
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
            val targetClass = requireNotNull(declaration.returnType.getClass()) {
                "Expected return type of map to be non-null."
            }

            val mapping = declaration.accept(MappingResolver(), Unit)

            val validation = MappingValidation.of(mapping)
            if (validation.isValid()) {
                declaration.body = with(createScope(declaration)) {
                    when (mapping) {
                        is ConstructorCallMapping -> {
                            context.blockBody(this.scope) {
                                val primaryConstructor = requireNotNull(targetClass.primaryConstructor) {
                                    "The target type must have a primary constructor."
                                }
                                +irReturn(irCallConstructor(primaryConstructor.symbol, emptyList()).apply {
                                    mapping.mappings.map { (target, source) ->
                                        val index = primaryConstructor.valueParameters.indexOf(target)
                                        putValueArgument(index, source.single().toIr(this@blockBody))
                                    }
                                })
                            }
                        }

                        is EnumMapping -> {
                            context.blockBody(this.scope) {
                                +irReturn(irWhen(mapping.targetType, mapping.mappings.map { (target, sources) ->
                                    val lhs = irGet(declaration.valueParameters.first())
                                    val rhs = irGetEnumValue(mapping.sourceType, sources.single().symbol)
                                    irBranch(irEqeqeq(lhs, rhs), irGetEnumValue(mapping.targetType, target.symbol))
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
                validation.problems().forEach { problem ->
                    context.messageCollector.error(problem, location(declaration))
                }
            }
        }
        return declaration
    }
}

fun MappingSource.toIr(builder: IrBuilderWithScope): IrExpression =
    when (this) {
        is PropertySource -> toIr(builder)
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
