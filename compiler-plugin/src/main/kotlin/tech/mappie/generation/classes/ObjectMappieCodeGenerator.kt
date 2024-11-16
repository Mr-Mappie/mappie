package tech.mappie.generation.classes

import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.typeOrFail
import tech.mappie.exceptions.MappiePanicException
import tech.mappie.generation.ClassMappieCodeGenerationModel
import tech.mappie.generation.CodeGenerationContext
import tech.mappie.generation.constructTransformation
import tech.mappie.referenceFunctionLet
import tech.mappie.referenceFunctionRequireNotNull
import tech.mappie.resolving.classes.sources.*
import tech.mappie.resolving.classes.targets.FunctionCallTarget
import tech.mappie.resolving.classes.targets.SetterTarget
import tech.mappie.resolving.classes.targets.ValueParameterTarget
import tech.mappie.util.*

class ObjectMappieCodeGenerator(private val context: CodeGenerationContext, private val model: ClassMappieCodeGenerationModel) {

    fun construct(scope: Scope): IrBody =
        context.pluginContext.blockBody(scope) {
            val constructor = model.constructor.symbol
            val call = irCallConstructor(constructor, emptyList()).apply {
                model.mappings.forEach { (target, source) ->
                    if (target is ValueParameterTarget) {
                        constructArgument(source, model.declaration.valueParameters)?.let { argument ->
                            val index = constructor.owner.valueParameters.indexOf(target.value)
                            putValueArgument(index, argument)
                        }
                    }
                }
            }

            val variable = createTmpVariable(call)

            model.mappings.forEach { (target, source) ->
                when (target) {
                    is SetterTarget -> {
                        +irCall(target.value.setter!!).apply {
                            dispatchReceiver = irGet(variable)
                            putValueArgument(0, constructArgument(source ,model.declaration.valueParameters))
                        }
                    }
                    is FunctionCallTarget -> {
                        +irCall(target.value).apply {
                            dispatchReceiver = irGet(variable)
                            putValueArgument(0, constructArgument(source, model.declaration.valueParameters))
                        }
                    }
                    else -> { /* Applied as a constructor call argument */ }
                }
            }

            +irReturn(irGet(variable))
        }

    private fun IrBuilderWithScope.constructArgument(source: ClassMappingSource, parameters: List<IrValueParameter>): IrExpression? =
        when (source) {
            is ExplicitPropertyMappingSource -> {
                val getter = // TODO: refactor
                    if (source.forceNonNull) {
                        irCall(this@ObjectMappieCodeGenerator.context.referenceFunctionRequireNotNull()).apply {
                            putValueArgument(0, irCall(source.reference.getter!!).apply {
                                dispatchReceiver = source.reference.dispatchReceiver
                                    ?: irGet(parameters.singleOrNull { it.type == (source.reference.type as IrSimpleType).arguments[0].typeOrFail }
                                        ?: throw MappiePanicException("Could not determine value parameter for property reference.", source.reference))
                            })
                        }
                    } else {
                        irCall(source.reference.getter!!).apply {
                            dispatchReceiver = source.reference.dispatchReceiver
                                ?: irGet(parameters.singleOrNull { it.type == (source.reference.type as IrSimpleType).arguments[0].typeOrFail }
                                ?: throw MappiePanicException("Could not determine value parameter for property reference.", source.reference))
                        }
                    }
                source.transformation?.let { constructTransformation(this@ObjectMappieCodeGenerator.context, it, getter) } ?: getter
            }
            is ExpressionMappingSource -> {
                irCall(this@ObjectMappieCodeGenerator.context.referenceFunctionLet()).apply {
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
                source.transformation?.let { constructTransformation(this@ObjectMappieCodeGenerator.context, it, getter) } ?: getter
            }
            is FunctionMappingSource -> {
                irCall(source.function.symbol).apply {
                    dispatchReceiver = irGet(parameters.first { it.name == source.parameter })
                }
            }
            is ParameterDefaultValueMappingSource -> {
                null
            }
        }
}