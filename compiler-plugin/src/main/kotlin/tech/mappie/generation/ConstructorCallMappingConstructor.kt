package tech.mappie.generation

import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.ir.IrFileEntry
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.util.constructors
import tech.mappie.MappieIrRegistrar
import tech.mappie.MappieIrRegistrar.Companion.context
import tech.mappie.mappieTerminate
import tech.mappie.resolving.ConstructorCallMapping
import tech.mappie.resolving.MappieDefinition
import tech.mappie.resolving.classes.*
import tech.mappie.resolving.classes.targets.MappieFunctionTarget
import tech.mappie.resolving.classes.targets.MappieSetterTarget
import tech.mappie.resolving.classes.targets.MappieTarget
import tech.mappie.resolving.classes.targets.MappieValueParameterTarget
import tech.mappie.util.*

class ConstructorCallMappingConstructor(
    private val generated: List<IrClass>,
    private val mapping: ConstructorCallMapping,
    declaration: IrFunction,
) : MappingConstructor(declaration) {

    override fun construct(scope: Scope) =
        context.blockBody(scope) {
            val constructorCall = irCallConstructor(mapping.symbol, emptyList()).apply {
                mapping.mappings.toList().forEach { (target, source) ->
                    if (target is MappieValueParameterTarget && source.single() !is DefaultArgumentSource) {
                        val index = mapping.symbol.owner.valueParameters.indexOf(target.value)
                        putValueArgument(index, generateValueArgument(file, source.single(), target, declaration))
                    }
                }
            }

            val variable = createTmpVariable(constructorCall)

            mapping.mappings.forEach { (target, source) ->
                when (target) {
                    is MappieSetterTarget -> {
                        +irCall(target.value.setter!!).apply {
                            dispatchReceiver = irGet(variable)
                            putValueArgument(0, generateValueArgument(file, source.single(), target, declaration))
                        }
                    }
                    is MappieFunctionTarget -> {
                        +irCall(target.value).apply {
                            dispatchReceiver = irGet(variable)
                            putValueArgument(0, generateValueArgument(file, source.single(),target, declaration))
                        }
                    }
                    is MappieValueParameterTarget -> { /* Applied as a constructor call argument */ }
                }
            }

            +irReturn(irGet(variable))
        }

    private fun IrBuilderWithScope.generateValueArgument(
        file: IrFileEntry,
        source: ObjectMappingSource,
        target: MappieTarget,
        function: IrFunction
    ): IrExpression {
        return when (source) {
            is ResolvedSource -> generateResolvedValueArgument(source, target, function)
            is PropertySource -> generatePropertyValueArgument(file, source, target, function.valueParameters)
            is ExpressionSource -> generateExpressionValueArgument(source, function.valueParameters)
            is ValueSource -> source.value
            is DefaultArgumentSource -> mappieTerminate("generateValueArgument called on DefaultArgumentSource. This is a bug.", location(function))
        }
    }

    private fun IrBuilderWithScope.generateResolvedValueArgument(
        source: ResolvedSource,
        target: MappieTarget,
        function: IrFunction
    ): IrExpression {
        val getter = irCall(source.property.function).apply {
            dispatchReceiver = irGet(source.property.holder)
        }
        return source.via?.let { generateTransformation(it, getter, source, target) } ?: getter
    }

    private fun IrBuilderWithScope.generatePropertyValueArgument(
        file: IrFileEntry,
        source: PropertySource,
        target: MappieTarget,
        parameters: List<IrValueParameter>
    ): IrExpression {
        val getter = irCall(source.getter).apply {
            dispatchReceiver = source.property.dispatchReceiver
                ?: irGet(parameters.singleOrNull { it.type == source.property.targetType(file) }
                    ?: mappieTerminate("Could not determine value parameters for property reference. Please use a property reference of an object instead of the class", location(file, source.property)))
        }
        return source.transformation?.let { generateTransformation(it, getter, source, target) } ?: getter
    }

    private fun IrBuilderWithScope.generateTransformation(
        transformation: MappieTransformation,
        value: IrExpression,
        source: ObjectMappingSource,
        target: MappieTarget,
    ) =
        when (transformation) {
            is MappieTransformTransformation -> {
                irCall(MappieIrRegistrar.context.referenceFunctionLet()).also { letCall ->
                    letCall.extensionReceiver = value
                    letCall.putValueArgument(0, transformation.function)
                }
            }
            is MappieViaTransformation -> {
                irIfNull(source.type, value,
                    irNull(),
                    irCall(transformation.function.symbol).apply {
                        dispatchReceiver = transformation.dispatchReceiver
                        putValueArgument(0, value)
                    },
                )
            }
            is MappieViaResolvedTransformation -> {
                val receiver = when (transformation.definition.clazz.kind) {
                    ClassKind.CLASS -> {
                        val constructor = transformation.definition.clazz.constructors.firstOrNull { it.valueParameters.isEmpty() }
                        if (constructor != null) {
                            irCallConstructor(constructor.symbol, emptyList())
                        } else {
                            mappieTerminate(
                                "Resolved mapping via ${transformation.definition.clazz.name.asString()}, but it does not have a constructor without arguments",
                                null, // TODO
                            )
                        }
                    }
                    ClassKind.OBJECT -> {
                        irGetObject(transformation.definition.clazz.symbol)
                    }
                    else -> {
                        mappieTerminate("", null) // TODO
                    }
                }
                irIfNull(source.type, receiver,
                    irNull(),
                    irCall(transformation.definition.function(source.type, target.type)).apply {
                        dispatchReceiver = receiver
                        putValueArgument(0, value)
                    }
                )
            }
            is MappieViaGeneratedMappieClassTransformation -> {
                val clazz = generated.find { it.name == transformation.definition.name }
                val definition = MappieDefinition(source.type, target.type, clazz!!)
                val receiver = when (clazz.kind) {
                    ClassKind.CLASS -> {
                        val constructor = clazz.constructors.firstOrNull { it.valueParameters.isEmpty() }
                        if (constructor != null) {
                            irCallConstructor(constructor.symbol, emptyList())
                        } else {
                            mappieTerminate(
                                "Resolved mapping via ${clazz.name.asString()}, but it does not have a constructor without arguments",
                                null, // TODO
                            )
                        }
                    }
                    ClassKind.OBJECT -> {
                        irGetObject(clazz.symbol)
                    }
                    else -> {
                        mappieTerminate("", null) // TODO
                    }
                }
                irIfNull(source.type, receiver,
                    irNull(),
                    irCall(definition.function(source.type, target.type)).apply {
                        dispatchReceiver = receiver
                        putValueArgument(0, value)
                    }
                )
            }
        }

    private fun IrBuilderWithScope.generateExpressionValueArgument(source: ExpressionSource, parameters: List<IrValueParameter>): IrFunctionAccessExpression {
        return irCall(MappieIrRegistrar.context.referenceFunctionLet()).apply {
            extensionReceiver = irGet(parameters.single())
            putValueArgument(0, source.expression)
        }
    }
}