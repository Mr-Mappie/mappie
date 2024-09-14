package tech.mappie.generation

import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.isNullable
import org.jetbrains.kotlin.ir.util.isObject
import org.jetbrains.kotlin.ir.util.primaryConstructor
import tech.mappie.MappieContext
import tech.mappie.exceptions.MappiePanicException
import tech.mappie.resolving.MappieDefinition
import tech.mappie.resolving.classes.sources.PropertyMappingTransformTranformation
import tech.mappie.resolving.classes.sources.PropertyMappingTransformation
import tech.mappie.resolving.classes.sources.PropertyMappingViaMapperTransformation
import tech.mappie.util.isList
import tech.mappie.util.isSet

fun IrBuilderWithScope.constructTransformation(context: MappieContext, transformation: PropertyMappingTransformation, value: IrExpression) =
    when (transformation) {
        is PropertyMappingTransformTranformation -> {
            irCall(context.referenceFunctionLet()).also { letCall ->
                letCall.extensionReceiver = value
                letCall.putValueArgument(0, transformation.function)
            }
        }
        is PropertyMappingViaMapperTransformation -> {
            irCall(transformation.selectTransformationFunction(value)).apply {
                dispatchReceiver = transformation.dispatchReceiver ?: instance(transformation.mapper)
                putValueArgument(0, value)
            }
        }
    }

private fun PropertyMappingViaMapperTransformation.selectTransformationFunction(value: IrExpression) =
    when { // TODO: nullable list, nullable set doesn't work with this approach
        value.type.isList() -> mapper.referenceMapListFunction()
        value.type.isSet() -> mapper.referenceMapSetFunction()
        value.type.isNullable() -> mapper.referenceMapNullableFunction()
        else -> mapper.referenceMapFunction()
    }

private fun IrBuilderWithScope.instance(mapper: MappieDefinition) =
    if (mapper.clazz.isObject) {
        irGetObject(mapper.clazz.symbol)
    } else if (mapper.clazz.primaryConstructor != null && mapper.clazz.primaryConstructor!!.valueParameters.isEmpty()) {
        irCallConstructor(mapper.clazz.primaryConstructor!!.symbol, emptyList())
    } else {
        throw MappiePanicException("Class ${mapper.clazz.name.asString()} should either be an object or has an primary constructor without parameters.")
    }

//fun IrBuilderWithScope.generateTransformation(
//    generated: List<IrClass>,
//    transformation: MappieTransformation,
//    value: IrExpression,
//    source: ObjectMappingSource,
//    target: ClassMappingTarget,
//    file: IrFileEntry,
//): IrExpression =
//    when (transformation) {
//        is MappieTransformOperator -> generateTransformOperator(transformation, value)
//        is MappieViaOperator -> generateViaOperator(transformation, target, value)
//        is MappieViaResolved -> generateViaResolved(transformation, source, target, value, file)
//        is MappieViaGeneratedClass -> generateViaGeneratedClass(transformation, generated, source, target, value, file)
//    }
//
//private fun IrBuilderWithScope.generateViaGeneratedClass(
//    transformation: MappieViaGeneratedClass,
//    generated: List<IrClass>,
//    source: ObjectMappingSource,
//    target: ClassMappingTarget,
//    value: IrExpression,
//    file: IrFileEntry,
//): IrExpression {
//    val clazz = generated.find { it.name == transformation.definition.name }
//        ?: mappieTerminate("Could not find generated class ${transformation.definition.name.asString()}. This is a bug.", null)
//
//    val definition = MappieDefinition(clazz)
//    return irCall(definition.function(value.type, target.type)).apply {
//        dispatchReceiver = getReceiver(clazz, file, source)
//        putValueArgument(0, value)
//    }
//}
//
//private fun IrBuilderWithScope.generateViaResolved(
//    transformation: MappieViaResolved,
//    source: ObjectMappingSource,
//    target: ClassMappingTarget,
//    value: IrExpression,
//    file: IrFileEntry,
//): IrExpression {
//    return irCall(transformation.definition.function(value.type, target.type)).apply {
//        dispatchReceiver = getReceiver(transformation.definition.clazz, file, source)
//        putValueArgument(0, value)
//    }
//}
//
//private fun IrBuilderWithScope.generateViaOperator(
//    transformation: MappieViaOperator,
//    target: ClassMappingTarget,
//    value: IrExpression,
//): IrFunctionAccessExpression {
//    return irCall(transformation.definition.function(value.type, target.type).symbol).apply {
//        dispatchReceiver = transformation.dispatchReceiver
//        putValueArgument(0, value)
//    }
//}
//
//private fun IrBuilderWithScope.generateTransformOperator(transformation: MappieTransformOperator, value: IrExpression) =
//    irCall(MappieIrRegistrar.context.referenceFunctionLet()).also { letCall ->
//        letCall.extensionReceiver = value
//        letCall.putValueArgument(0, transformation.function)
//    }
//
//private fun IrBuilderWithScope.getReceiver(
//    clazz: IrClass,
//    file: IrFileEntry,
//    source: ObjectMappingSource,
//) = when (clazz.kind) {
//    ClassKind.CLASS -> {
//        val constructor = clazz.constructors.firstOrNull { it.valueParameters.isEmpty() }
//        if (constructor != null) {
//            irCallConstructor(constructor.symbol, emptyList())
//        } else {
//            mappieTerminate(
//                "Resolved mapping via ${clazz.name.asString()}, but it does not have a constructor without arguments",
//                location(file, source.origin),
//            )
//        }
//    }
//    ClassKind.OBJECT -> {
//        irGetObject(clazz.symbol)
//    }
//
//    else -> {
//        mappieTerminate(
//            "Resolved mapping via ${clazz.name.asString()}, but is an ${clazz.kind.codeRepresentation} and either an object or class was expected",
//            location(file, source.origin)
//        )
//    }
//}
