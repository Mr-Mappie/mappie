package tech.mappie.generation

import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.ir.IrFileEntry
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
import tech.mappie.util.location
import tech.mappie.util.referenceFunctionLet

fun IrBuilderWithScope.generateTransformation(
    generated: List<IrClass>,
    transformation: MappieTransformation,
    value: IrExpression,
    source: ObjectMappingSource,
    target: MappieTarget,
    file: IrFileEntry,
): IrExpression =
    when (transformation) {
        is MappieTransformOperator -> generateTransformOperator(transformation, value)
        is MappieViaOperator -> generateViaOperator(transformation, target, value)
        is MappieViaResolved -> generateViaResolved(transformation, source, target, value, file)
        is MappieViaGeneratedClass -> generateViaGeneratedClass(transformation, generated, source, target, value, file)
    }

private fun IrBuilderWithScope.generateViaGeneratedClass(
    transformation: MappieViaGeneratedClass,
    generated: List<IrClass>,
    source: ObjectMappingSource,
    target: MappieTarget,
    value: IrExpression,
    file: IrFileEntry,
): IrExpression {
    val clazz = generated.find { it.name == transformation.definition.name }
        ?: mappieTerminate("Could not find generated class ${transformation.definition.name.asString()}. This is a bug.", null)

    val definition = MappieDefinition(clazz)
    return irCall(definition.function(value.type, target.type)).apply {
        dispatchReceiver = getReceiver(clazz, file, source)
        putValueArgument(0, value)
    }
}

private fun IrBuilderWithScope.generateViaResolved(
    transformation: MappieViaResolved,
    source: ObjectMappingSource,
    target: MappieTarget,
    value: IrExpression,
    file: IrFileEntry,
): IrExpression {
    return irCall(transformation.definition.function(value.type, target.type)).apply {
        dispatchReceiver = getReceiver(transformation.definition.clazz, file, source)
        putValueArgument(0, value)
    }
}

private fun IrBuilderWithScope.generateViaOperator(
    transformation: MappieViaOperator,
    target: MappieTarget,
    value: IrExpression,
): IrFunctionAccessExpression {
    return irCall(transformation.definition.function(value.type, target.type).symbol).apply {
        dispatchReceiver = transformation.dispatchReceiver
        putValueArgument(0, value)
    }
}

private fun IrBuilderWithScope.generateTransformOperator(transformation: MappieTransformOperator, value: IrExpression) =
    irCall(MappieIrRegistrar.context.referenceFunctionLet()).also { letCall ->
        letCall.extensionReceiver = value
        letCall.putValueArgument(0, transformation.function)
    }

private fun IrBuilderWithScope.getReceiver(
    clazz: IrClass,
    file: IrFileEntry,
    source: ObjectMappingSource,
) = when (clazz.kind) {
    ClassKind.CLASS -> {
        val constructor = clazz.constructors.firstOrNull { it.valueParameters.isEmpty() }
        if (constructor != null) {
            irCallConstructor(constructor.symbol, emptyList())
        } else {
            mappieTerminate(
                "Resolved mapping via ${clazz.name.asString()}, but it does not have a constructor without arguments",
                location(file, source.origin),
            )
        }
    }
    ClassKind.OBJECT -> {
        irGetObject(clazz.symbol)
    }

    else -> {
        mappieTerminate(
            "Resolved mapping via ${clazz.name.asString()}, but is an ${clazz.kind.codeRepresentation} and either an object or class was expected",
            location(file, source.origin)
        )
    }
}
