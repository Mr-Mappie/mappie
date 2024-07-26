package tech.mappie.generation

import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression
import org.jetbrains.kotlin.ir.util.constructors
import tech.mappie.MappieIrRegistrar
import tech.mappie.mappieTerminate
import tech.mappie.resolving.MappieDefinition
import tech.mappie.resolving.classes.*
import tech.mappie.resolving.classes.targets.MappieTarget
import tech.mappie.util.referenceFunctionLet

fun IrBuilderWithScope.generateTransformation(
    generated: List<IrClass>,
    transformation: MappieTransformation,
    value: IrExpression,
    source: ObjectMappingSource,
    target: MappieTarget,
): IrExpression =
    when (transformation) {
        is MappieTransformOperator -> generateTransformOperator(transformation, value)
        is MappieViaOperator -> generateViaOperator(transformation, target, value)
        is MappieViaResolved -> generateViaResolved(transformation, target, value)
        is MappieViaGeneratedClass -> generateViaGeneratedClass(transformation, generated, source, target, value)
    }

private fun IrBuilderWithScope.generateViaGeneratedClass(
    transformation: MappieViaGeneratedClass,
    generated: List<IrClass>,
    source: ObjectMappingSource,
    target: MappieTarget,
    value: IrExpression,
): IrExpression {
    val clazz = generated.find { it.name == transformation.definition.name }
        ?: mappieTerminate("Could not find generated class ${transformation.definition.name.asString()}. This is a bug.", null)

    val definition = MappieDefinition(clazz)
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
    return irIfNull(source.type, receiver,
        irNull(),
        irCall(definition.function(value.type, target.type)).apply {
            dispatchReceiver = receiver
            putValueArgument(0, value)
        }
    )
}

private fun IrBuilderWithScope.generateViaResolved(
    transformation: MappieViaResolved,
    target: MappieTarget,
    value: IrExpression,
): IrExpression {
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

    val transformation = transformation.definition.function(value.type, target.type)
    return irCall(transformation).apply {
        dispatchReceiver = receiver
        putValueArgument(0, value)
    }
}

private fun IrBuilderWithScope.generateViaOperator(
    transformation: MappieViaOperator,
    target: MappieTarget,
    value: IrExpression,
): IrFunctionAccessExpression {
    val function = transformation.definition.function(value.type, target.type)
    return irCall(function.symbol).apply {
        dispatchReceiver = transformation.dispatchReceiver
        putValueArgument(0, value)
    }
}

private fun IrBuilderWithScope.generateTransformOperator(transformation: MappieTransformOperator, value: IrExpression) =
    irCall(MappieIrRegistrar.context.referenceFunctionLet()).also { letCall ->
        letCall.extensionReceiver = value
        letCall.putValueArgument(0, transformation.function)
    }
