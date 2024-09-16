package tech.mappie.generation

import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.isNullable
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.isObject
import org.jetbrains.kotlin.ir.util.primaryConstructor
import tech.mappie.exceptions.MappiePanicException
import tech.mappie.referenceFunctionLet
import tech.mappie.resolving.classes.sources.GeneratedViaMapperTransformation
import tech.mappie.resolving.classes.sources.PropertyMappingTransformTranformation
import tech.mappie.resolving.classes.sources.PropertyMappingTransformation
import tech.mappie.resolving.classes.sources.PropertyMappingViaMapperTransformation
import tech.mappie.util.*

fun IrBuilderWithScope.constructTransformation(context: CodeGenerationContext, transformation: PropertyMappingTransformation, value: IrExpression) =
    when (transformation) {
        is PropertyMappingTransformTranformation -> {
            irCall(context.referenceFunctionLet()).also { letCall ->
                letCall.extensionReceiver = value
                letCall.putValueArgument(0, transformation.function)
            }
        }
        is PropertyMappingViaMapperTransformation -> {
            irCall(transformation.selectTransformationFunction(value)).apply {
                dispatchReceiver = transformation.dispatchReceiver ?: instance(transformation.mapper.clazz)
                putValueArgument(0, value)
            }
        }
        is GeneratedViaMapperTransformation -> {
            val clazz = context.generated[transformation.source.type to transformation.target.type]!!
            irCall(clazz.selectTransformationFunction(value)).apply {
                dispatchReceiver = instance(clazz)
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

private fun IrClass.selectTransformationFunction(value: IrExpression) =
    when { // TODO: nullable list, nullable set doesn't work with this approach
        value.type.isList() -> functions.first { it.isMappieMapListFunction() }
        value.type.isSet() -> functions.first { it.isMappieMapSetFunction() }
        value.type.isNullable() -> functions.first { it.isMappieMapNullableFunction() }
        else -> functions.first { it.isMappieMapFunction() }
    }

private fun IrBuilderWithScope.instance(clazz: IrClass) =
    if (clazz.isObject) {
        irGetObject(clazz.symbol)
    } else if (clazz.primaryConstructor != null && clazz.primaryConstructor!!.valueParameters.isEmpty()) {
        irCallConstructor(clazz.primaryConstructor!!.symbol, emptyList())
    } else {
        throw MappiePanicException("Class ${clazz.name.asString()} should either be an object or has an primary constructor without parameters.")
    }
