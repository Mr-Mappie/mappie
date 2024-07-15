package tech.mappie.generation

import org.jetbrains.kotlin.ir.IrFileEntry
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.isObject
import tech.mappie.MappieIrRegistrar
import tech.mappie.MappieIrRegistrar.Companion.context
import tech.mappie.mappieTerminate
import tech.mappie.resolving.ConstructorCallMapping
import tech.mappie.resolving.classes.*
import tech.mappie.resolving.classes.targets.MappieFunctionTarget
import tech.mappie.resolving.classes.targets.MappieSetterTarget
import tech.mappie.resolving.classes.targets.MappieValueParameterTarget
import tech.mappie.util.location
import tech.mappie.util.referenceLetFunction
import tech.mappie.util.targetType

class ConstructorCallMappingConstructor(private val mapping: ConstructorCallMapping, declaration: IrFunction)
    : MappingConstructor( declaration) {

    override fun construct(scope: Scope) =
        context.blockBody(scope) {
            val constructorCall = irCallConstructor(mapping.symbol, emptyList()).apply {
                mapping.mappings.toList().forEach { (target, source) ->
                    if (target is MappieValueParameterTarget) {
                        val index = mapping.symbol.owner.valueParameters.indexOf(target.value)
                        putValueArgument(index, generateValueArgument(file, source.single(), declaration))
                    }
                }
            }

            val variable = createTmpVariable(constructorCall)

            mapping.mappings.forEach { (target, source) ->
                when (target) {
                    is MappieSetterTarget -> {
                        +irCall(target.value.setter!!).apply {
                            dispatchReceiver = irGet(variable)
                            putValueArgument(0, generateValueArgument(file, source.single(), declaration))
                        }
                    }
                    is MappieFunctionTarget -> {
                        +irCall(target.value).apply {
                            dispatchReceiver = irGet(variable)
                            putValueArgument(0, generateValueArgument(file, source.single(), declaration))
                        }
                    }
                    is MappieValueParameterTarget -> { /* Applied as a constructor call argument */ }
                }
            }

            +irReturn(irGet(variable))
        }

    private fun IrBuilderWithScope.generateValueArgument(file: IrFileEntry, source: ObjectMappingSource, function: IrFunction): IrExpression {
        return when (source) {
            is ResolvedSource -> generateResolvedValueArgument(source, function)
            is PropertySource -> generatePropertyValueArgument(file, source, function.valueParameters)
            is ExpressionSource -> generateExpressionValueArgument(source, function.valueParameters)
            is ValueSource -> source.value
        }
    }

    private fun IrBuilderWithScope.generateResolvedValueArgument(source: ResolvedSource, function: IrFunction): IrFunctionAccessExpression {
        val getter = irCall(source.property.function).apply {
            dispatchReceiver = irGet(source.property.holder)
        }
        return source.via?.let { (clazz, via) ->
            irCall(via).apply {
                dispatchReceiver = when {
                    clazz.isObject -> irGetObject(clazz.symbol)
                    else -> {
                        val constructor = clazz.constructors.firstOrNull { it.valueParameters.isEmpty() }
                        if (constructor != null) {
                            irCallConstructor(constructor.symbol, emptyList())
                        } else {
                            mappieTerminate("Resolved mapping via ${clazz.name.asString()}, but it does not have a constructor without arguments", location(function))
                        }
                    }
                }
                putValueArgument(0, getter)
            }
        } ?: getter
    }

    private fun IrBuilderWithScope.generatePropertyValueArgument(file: IrFileEntry, source: PropertySource, parameters: List<IrValueParameter>): IrFunctionAccessExpression {
        val getter = irCall(source.getter).apply {
            dispatchReceiver = source.property.dispatchReceiver
                ?: irGet(parameters.singleOrNull { it.type == source.property.targetType(file) }
                    ?: mappieTerminate("Could not determine value parameters for property reference. Please use a property reference of an object instead of the class", location(file, source.property)))
        }
        return source.transformation?.let { transformation ->
            when (transformation) {
                is MappieTransformTransformation -> {
                    irCall(MappieIrRegistrar.context.referenceLetFunction()).also { letCall ->
                        letCall.extensionReceiver = getter
                        letCall.putValueArgument(0, transformation.function)
                    }
                }
                is MappieViaTransformation -> {
                    irCall(transformation.function.symbol).apply {
                        dispatchReceiver = transformation.dispatchReceiver
                        putValueArgument(0, getter)
                    }
                }
            }
        } ?: getter
    }

    private fun IrBuilderWithScope.generateExpressionValueArgument(source: ExpressionSource, parameters: List<IrValueParameter>): IrFunctionAccessExpression {
        return irCall(MappieIrRegistrar.context.referenceLetFunction()).apply {
            extensionReceiver = irGet(parameters.single())
            putValueArgument(0, source.expression)
        }
    }
}