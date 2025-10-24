package tech.mappie.ir.generation.classes

import org.jetbrains.kotlin.backend.common.lower.DeclarationIrBuilder
import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.makeNotNull
import org.jetbrains.kotlin.ir.types.typeOrFail
import org.jetbrains.kotlin.ir.types.typeOrNull
import org.jetbrains.kotlin.ir.util.getKFunctionType
import tech.mappie.exceptions.MappiePanicException.Companion.panic
import tech.mappie.ir.generation.ClassMappieCodeGenerationModel
import tech.mappie.ir.generation.CodeGenerationContext
import tech.mappie.ir.generation.constructTransformation
import tech.mappie.ir.reporting.pretty
import tech.mappie.referenceFunctionLet
import tech.mappie.referenceFunctionRequireNotNull
import tech.mappie.ir.resolving.classes.sources.*
import tech.mappie.ir.resolving.classes.targets.ClassMappingTarget
import tech.mappie.ir.resolving.classes.targets.FunctionCallTarget
import tech.mappie.ir.resolving.classes.targets.SetterTarget
import tech.mappie.ir.resolving.classes.targets.ValueParameterTarget
import tech.mappie.ir.util.blockBody
import tech.mappie.ir.util.irLambda
import tech.mappie.referenceFunctionRun

class ObjectMappieCodeGenerator(private val context: CodeGenerationContext, private val model: ClassMappieCodeGenerationModel) {

    fun lambda(scope: Scope): IrCall =
        with(context.pluginContext.irBuiltIns.createIrBuilder(scope.scopeOwnerSymbol)) {
            irCall(this@ObjectMappieCodeGenerator.context.referenceFunctionRun()).apply {
                arguments[0] = irLambda(model.declaration.returnType, model.declaration.returnType) {
                    content()
                }
            }
        }

    fun body(scope: Scope): IrBlockBody =
        context.pluginContext.blockBody(scope) {
            content()
        }

    private fun IrBlockBodyBuilder.content() {
        val constructor = model.constructor.symbol
        val regularParameters = model.declaration.parameters.filter { it.kind == IrParameterKind.Regular }
        val typeArguments = (model.declaration.returnType.type as IrSimpleType).arguments.map { it.typeOrNull ?: context.irBuiltIns.anyType }

        val call = irCallConstructor(constructor, typeArguments).apply {
            model.mappings.forEach { (target, source) ->
                if (target is ValueParameterTarget) {
                    constructArgument(source, target, regularParameters)?.let { argument ->
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
                        arguments[1] = constructArgument(source, target, regularParameters)
                    }
                }

                is FunctionCallTarget -> {
                    +irCall(target.value).apply {
                        dispatchReceiver = irGet(variable)
                        arguments[1] = constructArgument(source, target, regularParameters)
                    }
                }

                else -> { /* Applied as a constructor call argument */
                }
            }
        }

        +irReturn(irGet(variable))
    }

    fun construct(builder: DeclarationIrBuilder): IrCall {
            return builder.irCall(context.referenceFunctionRun()).apply {
                arguments[0] = builder.irLambda(model.declaration.returnType, model.declaration.returnType) {
                }
            }
    }

    private fun IrBuilderWithScope.constructArgument(source: ClassMappingSource, target: ClassMappingTarget, parameters: List<IrValueParameter>): IrExpression? =
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
                source.transformation?.let { constructTransformation(this@ObjectMappieCodeGenerator.context, it, getter, target) } ?: getter
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
                source.transformation?.let { constructTransformation(
                    this@ObjectMappieCodeGenerator.context,
                    it,
                    getter,
                    target
                ) } ?: getter
            }
            is FunctionMappingSource -> {
                irCall(source.function.symbol).apply {
                    dispatchReceiver = irGet(parameters.first { it.name == source.parameter })
                }
            }
            is ParameterValueMappingSource -> {
                val getter = irGet(parameters.first { it.name == source.parameter })
                source.transformation?.let { constructTransformation(
                    this@ObjectMappieCodeGenerator.context,
                    it,
                    getter,
                    target
                ) }
                    ?: getter
            }
            is ParameterDefaultValueMappingSource -> {
                null
            }
        }
}