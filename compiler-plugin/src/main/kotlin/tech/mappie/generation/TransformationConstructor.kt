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
