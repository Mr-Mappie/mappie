package tech.mappie.generation

import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.expressions.IrExpression
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
        is MappieTransformOperator -> generateTransformOperator(value, transformation)
        is MappieViaOperator -> generateViaOperator(source, value, transformation)
        is MappieViaResolved -> generateViaResolved(transformation, source, target, value)
        is MappieViaGeneratedClass -> generateViaGeneratedClass(generated, transformation, source, target, value)
    }

private fun IrBuilderWithScope.generateViaGeneratedClass(
    generated: List<IrClass>,
    transformation: MappieViaGeneratedClass,
    source: ObjectMappingSource,
    target: MappieTarget,
    value: IrExpression,
): IrExpression {
    val clazz = generated.find { it.name == transformation.definition.name }
        ?: mappieTerminate("Could not find generated class ${transformation.definition.name.asString()}. This is a bug.", null)

    val definition = MappieDefinition(source.type, target.type, clazz)
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
        irCall(definition.function(source.type, target.type)).apply {
            dispatchReceiver = receiver
            putValueArgument(0, value)
        }
    )
}

private fun IrBuilderWithScope.generateViaResolved(
    transformation: MappieViaResolved,
    source: ObjectMappingSource,
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
    return irIfNull(source.type, receiver,
        irNull(),
        irCall(transformation.definition.function(source.type, target.type)).apply {
            dispatchReceiver = receiver
            putValueArgument(0, value)
        }
    )
}

private fun IrBuilderWithScope.generateViaOperator(source: ObjectMappingSource, value: IrExpression, transformation: MappieViaOperator) =
    irIfNull(source.type, value,
        irNull(),
        irCall(transformation.function.symbol).apply {
            dispatchReceiver = transformation.dispatchReceiver
            putValueArgument(0, value)
        },
    )

private fun IrBuilderWithScope.generateTransformOperator(value: IrExpression, transformation: MappieTransformOperator) =
    irCall(MappieIrRegistrar.context.referenceFunctionLet()).also { letCall ->
        letCall.extensionReceiver = value
        letCall.putValueArgument(0, transformation.function)
    }
