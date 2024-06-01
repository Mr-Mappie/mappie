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
            val primaryConstructor = requireNotNull(targetClass.primaryConstructor) {
                "The target type must have a primary constructor."
            }

            val mapping = declaration.accept(MappingResolver(pluginContext), Unit)

            declaration.body = with (createScope(declaration)) {
                when (mapping) {
                    is ConstructorCallMapping -> {
                        pluginContext.blockBody(this.scope) {
                            +irReturn(irCallConstructor(primaryConstructor.symbol, emptyList()).apply {
                                mapping.sources.mapIndexed { index, source ->
                                    putValueArgument(index, source.toIr(this@blockBody))
                                }
                            })
                        }
                    }

// VAR IR_TEMPORARY_VARIABLE name:tmp0_subject type:testing.Color [val]
// GET_VAR 'from: testing.Color declared in testing.testy' type=testing.Color origin=null
//     WHEN type=testing.Colour origin=WHEN
//     BRANCH
// if: CALL 'public final fun EQEQ (arg0: kotlin.Any?, arg1: kotlin.Any?): kotlin.Boolean declared in kotlin.internal.ir' type=kotlin.Boolean origin=EQEQ
//     arg0: GET_VAR 'val tmp0_subject: testing.Color [val] declared in testing.testy' type=testing.Color origin=null
//     arg1: GET_ENUM 'ENUM_ENTRY name:RED' type=testing.Color
//     then: GET_ENUM 'ENUM_ENTRY name:RED' type=testing.Colour
//     BRANCH
// if: CONST Boolean type=kotlin.Boolean value=true
//     then: CALL 'public final fun noWhenBranchMatchedException (): kotlin.Nothing declared in kotlin.internal.ir' type=kotlin.Nothing origin=null

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
