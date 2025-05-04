package tech.mappie.ir.generation.classes

import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.makeNotNull
import org.jetbrains.kotlin.ir.types.typeOrFail
import tech.mappie.exceptions.MappiePanicException.Companion.panic
import tech.mappie.ir.generation.ClassUpdateCodeGenerationModel
import tech.mappie.ir.generation.CodeGenerationContext
import tech.mappie.ir.generation.constructTransformation
import tech.mappie.referenceFunctionLet
import tech.mappie.referenceFunctionRequireNotNull
import tech.mappie.ir.resolving.classes.sources.*
import tech.mappie.ir.resolving.classes.targets.FunctionCallTarget
import tech.mappie.ir.resolving.classes.targets.SetterTarget
import tech.mappie.ir.util.blockBody

class ObjectUpdateMappieCodeGenerator(private val context: CodeGenerationContext, private val model: ClassUpdateCodeGenerationModel) {

    fun construct(scope: Scope): IrBody =
        context.pluginContext.blockBody(scope) {
            val parameter = model.declaration.valueParameters.single { it.name == model.source }
            model.mappings.forEach { (target, source) ->
                when (target) {
                    is SetterTarget -> {
                        +irCall(target.value.setter!!).apply {
                            dispatchReceiver = irGet(parameter)
                            putValueArgument(0, constructArgument(source, model.declaration.valueParameters))
                        }
                    }
                    is FunctionCallTarget -> {
                        +irCall(target.value).apply {
                            dispatchReceiver = irGet(parameter)
                            putValueArgument(0, constructArgument(source, model.declaration.valueParameters))
                        }
                    }
                    else -> { /* Irrelevant for update */ }
                }
            }

            +irReturn(irGet(parameter))
        }

    private fun IrBuilderWithScope.constructArgument(source: ClassMappingSource, parameters: List<IrValueParameter>): IrExpression? =
        when (source) {
            is ExplicitPropertyMappingSource -> {
                val receiver = source.reference.dispatchReceiver
                        ?: irGet(parameters.singleOrNull { it.type == (source.reference.type as IrSimpleType).arguments[0].typeOrFail }
                            ?: panic("Could not determine value parameter for property reference.", source.reference))

                val getter = if (source.forceNonNull) {
                    irCall(this@ObjectUpdateMappieCodeGenerator.context.referenceFunctionRequireNotNull(), source.reference.getter!!.owner.returnType.makeNotNull()).apply {
                        putValueArgument(0, irCall(source.reference.getter!!).apply {
                            dispatchReceiver = receiver
                        })
                    }
                } else {
                    irCall(source.reference.getter!!).apply {
                        dispatchReceiver = receiver
                    }
                }
                source.transformation?.let { constructTransformation(this@ObjectUpdateMappieCodeGenerator.context, it, getter) } ?: getter
            }
            is ExpressionMappingSource -> {
                irCall(this@ObjectUpdateMappieCodeGenerator.context.referenceFunctionLet()).apply {
                    extensionReceiver = irGet(parameters.single())
                    putValueArgument(0, source.expression)
                }
            }
            is ValueMappingSource -> {
                source.expression
            }
            is ImplicitPropertyMappingSource -> {
                val getter = irCall(source.property.getter!!).apply {
                    dispatchReceiver = irGet(parameters.first { it.name == source.parameter })
                }
                source.transformation?.let { constructTransformation(this@ObjectUpdateMappieCodeGenerator.context, it, getter) } ?: getter
            }
            is FunctionMappingSource -> {
                irCall(source.function.symbol).apply {
                    dispatchReceiver = irGet(parameters.first { it.name == source.parameter })
                }
            }
            is ParameterValueMappingSource -> {
                val getter = irGet(parameters.first { it.name == source.parameter })
                source.transformation?.let { constructTransformation(this@ObjectUpdateMappieCodeGenerator.context, it, getter) }
                    ?: getter
            }
            is ParameterDefaultValueMappingSource -> {
                null
            }
        }
}