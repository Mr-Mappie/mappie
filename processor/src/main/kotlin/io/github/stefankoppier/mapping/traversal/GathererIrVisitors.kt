package io.github.stefankoppier.mapping.traversal

import io.github.stefankoppier.mapping.MappingPluginContext
import io.github.stefankoppier.mapping.resolver.*
import io.github.stefankoppier.mapping.util.isSubclassOfFqName
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.*
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
            val primaryConstructor = requireNotNull(targetClass.primaryConstructor) {
                "The target type must have a primary constructor."
            }

            val targets = declaration.accept(MappingResolver(), mutableListOf())

            declaration.body = with (createScope(declaration)) {
                pluginContext.blockBody(this.scope) {
                    +irReturn(irCallConstructor(primaryConstructor.symbol, emptyList()).apply {
                        targets.mapIndexed { index, source ->
                            putValueArgument(index, source.toIr(this@blockBody))
                        }
                    })
                }
            }

        }
        return declaration
    }
}

fun MappingSource.toIr(builder: IrBuilderWithScope): IrExpression =
    when (this) {
        is PropertySource -> toIr(builder)
        is ConstantSource<*> -> toIr(builder)
    }

fun PropertySource.toIr(builder: IrBuilderWithScope) =
    builder.irCall(property).apply {
        dispatchReceiver = builder.irGet(type, dispatchReceiverSymbol)
    }

fun ConstantSource<String>.toIr(builder: IrBuilderWithScope) =
    when (type) {
        builder.context.irBuiltIns.stringType -> IrConstImpl.string(SYNTHETIC_OFFSET, SYNTHETIC_OFFSET, type, value)
        else -> TODO("Unimplemented type")
    }