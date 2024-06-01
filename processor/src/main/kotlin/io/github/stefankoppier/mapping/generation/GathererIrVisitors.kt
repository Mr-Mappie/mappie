package io.github.stefankoppier.mapping.generation

import io.github.stefankoppier.mapping.MappingPluginContext
import io.github.stefankoppier.mapping.resolving.*
import io.github.stefankoppier.mapping.resolving.classes.ConstantSource
import io.github.stefankoppier.mapping.resolving.classes.MappingSource
import io.github.stefankoppier.mapping.resolving.classes.PropertySource
import io.github.stefankoppier.mapping.util.isSubclassOfFqName
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.linkage.issues.IrDeserializationException
import org.jetbrains.kotlin.backend.common.lower.irThrow
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrGetEnumValueImpl
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.types.impl.IrSimpleTypeImpl
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.Name

@OptIn(UnsafeDuringIrConstructionAPI::class)
class IrTransformer(private val pluginContext: MappingPluginContext): IrElementTransformerVoidWithContext() {

    override fun visitFileNew(declaration: IrFile): IrFile {
        val result = super.visitFileNew(declaration)
        return result
    }

    override fun visitClassNew(declaration: IrClass): IrStatement {
        if (!declaration.isSubclassOfFqName("io.github.stefankoppier.mapping.annotations.Mapper")) {
            return declaration
        }

        return super.visitClassNew(declaration)
    }

    override fun visitFunctionNew(declaration: IrFunction): IrStatement {
        if (declaration.name == Name.identifier("map")) {
            val targetClass = requireNotNull(declaration.returnType.getClass()) {
                "Expected return type of map to be non-null."
            }

            val mapping = declaration.accept(MappingResolver(pluginContext), Unit)

            declaration.body = with (createScope(declaration)) {
                when (mapping) {
                    is ConstructorCallMapping -> {
                        pluginContext.blockBody(this.scope) {
                            val primaryConstructor = requireNotNull(targetClass.primaryConstructor) {
                                "The target type must have a primary constructor."
                            }
                            +irReturn(irCallConstructor(primaryConstructor.symbol, emptyList()).apply {
                                mapping.sources.mapIndexed { index, source ->
                                    putValueArgument(index, source.toIr(this@blockBody))
                                }
                            })
                        }
                    }
                    is EnumMapping -> {
                        pluginContext.blockBody(this.scope) {
                            +irReturn(irWhen(mapping.targetType, mapping.sources.map { source ->
                                val target = mapping.targets.first { it.name == source.name }
                                val lhs = irGet(declaration.valueParameters.first())
                                val rhs = IrGetEnumValueImpl(SYNTHETIC_OFFSET, SYNTHETIC_OFFSET, mapping.sourceType, source.symbol)
                                irBranch(irEqeqeq(lhs, rhs), IrGetEnumValueImpl(SYNTHETIC_OFFSET, SYNTHETIC_OFFSET, mapping.targetType, target.symbol))
                            } + irElseBranch(irCall(context.irBuiltIns.noWhenBranchMatchedExceptionSymbol))))
                        }
                    }
                    is SingleValueMapping -> {
                        pluginContext.blockBody(this.scope) {
                            +irReturn(mapping.value)
                        }
                    }
                }
            }

        }
        return declaration
    }
}

fun MappingSource.toIr(builder: IrBuilderWithScope): IrExpression =
    when (this) {
        is PropertySource -> toIr(builder)
        is ConstantSource<*> -> this.value
    }

fun PropertySource.toIr(builder: IrBuilderWithScope) =
    builder.irCall(property).apply {
        dispatchReceiver = builder.irGet(type, dispatchReceiverSymbol)
    }
