package tech.mappie.ir.generation.classes

import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.makeNotNull
import org.jetbrains.kotlin.ir.types.typeOrFail
import org.jetbrains.kotlin.ir.util.getKFunctionType
import tech.mappie.exceptions.MappiePanicException.Companion.panic
import tech.mappie.ir.generation.ClassMappieCodeGenerationModel
import tech.mappie.ir.generation.CodeGenerationContext
import tech.mappie.ir.generation.constructTransformation
import tech.mappie.ir.reporting.pretty
import tech.mappie.referenceFunctionLet
import tech.mappie.referenceFunctionRequireNotNull
import tech.mappie.ir.resolving.classes.sources.*
import tech.mappie.ir.resolving.classes.targets.FunctionCallTarget
import tech.mappie.ir.resolving.classes.targets.SetterTarget
import tech.mappie.ir.resolving.classes.targets.ValueParameterTarget
import tech.mappie.ir.util.blockBody
import tech.mappie.ir.util.irLambda

class ObjectMappieCodeGenerator(private val context: CodeGenerationContext, private val model: ClassMappieCodeGenerationModel) {

    fun construct(scope: Scope): IrBody =
        context.pluginContext.blockBody(scope) {
            val constructor = model.constructor.symbol
            val call = irCallConstructor(constructor, emptyList()).apply {
                model.mappings.forEach { (target, source) ->
                    if (target is ValueParameterTarget) {
                        constructArgument(source, model.declaration.parameters.filter { it.kind == IrParameterKind.Regular })?.let { argument ->
                            arguments[target.value.indexInParameters] = argument
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
                            arguments[1] = constructArgument(source, model.declaration.parameters.filter { it.kind == IrParameterKind.Regular })
                        }
                    }
                    is FunctionCallTarget -> {
                        +irCall(target.value).apply {
                            dispatchReceiver = irGet(variable)
                            arguments[1] = constructArgument(source, model.declaration.parameters.filter { it.kind == IrParameterKind.Regular })
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
                val receiver = source.reference.dispatchReceiver
                        ?: irGet(parameters.singleOrNull { it.type == (source.reference.type as IrSimpleType).arguments[0].typeOrFail }
                            ?: panic("Could not determine value parameter for property reference.", source.reference))

                val getter = if (source.forceNonNull) {
                    irCall(this@ObjectMappieCodeGenerator.context.referenceFunctionRequireNotNull(), source.reference.getter!!.owner.returnType.makeNotNull()).apply {
                        arguments[0] = irCall(source.reference.getter!!).apply {
                            dispatchReceiver = receiver
                        };
                        arguments[1] = this@constructArgument.irLambda(context.irBuiltIns.anyType, context.irBuiltIns.getKFunctionType(context.irBuiltIns.stringType, emptyList())) {
                            +irReturn(irString("Reference ${source.reference.pretty()} must be non-null."))
                        }
                    }
                } else {
                    irCall(source.reference.getter!!).apply {
                        dispatchReceiver = receiver
                    }
                }
                source.transformation?.let { constructTransformation(this@ObjectMappieCodeGenerator.context, it, getter) } ?: getter
            }
            is ExpressionMappingSource -> {
                irCall(this@ObjectMappieCodeGenerator.context.referenceFunctionLet()).apply {
                    arguments[0] = irGet(parameters.single())
                    arguments[1] = source.expression
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
            is ParameterValueMappingSource -> {
                val getter = irGet(parameters.first { it.name == source.parameter })
                source.transformation?.let { constructTransformation(this@ObjectMappieCodeGenerator.context, it, getter) }
                    ?: getter
            }
            is ParameterDefaultValueMappingSource -> {
                null
            }
        }
}