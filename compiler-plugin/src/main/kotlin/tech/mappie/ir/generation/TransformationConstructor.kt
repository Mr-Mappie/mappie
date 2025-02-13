package tech.mappie.ir.generation

import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.isNullable
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.isObject
import org.jetbrains.kotlin.ir.util.primaryConstructor
import tech.mappie.exceptions.MappiePanicException.Companion.panic
import tech.mappie.referenceFunctionLet
import tech.mappie.ir.resolving.classes.sources.GeneratedViaMapperTransformation
import tech.mappie.ir.resolving.classes.sources.PropertyMappingTransformTranformation
import tech.mappie.ir.resolving.classes.sources.PropertyMappingTransformation
import tech.mappie.ir.resolving.classes.sources.PropertyMappingViaMapperTransformation
import tech.mappie.ir.util.*

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
            val clazz = context.generated[transformation.source.type.mappieType() to transformation.target.type.mappieType()]!!
            irCall(clazz.selectTransformationFunction(value)).apply {
                dispatchReceiver = instance(clazz)
                putValueArgument(0, value)
            }
        }
    }

private fun PropertyMappingViaMapperTransformation.selectTransformationFunction(value: IrExpression) =
    when {
        value.type.isList() && value.type.isNullable() -> mapper.referenceMapNullableListFunction()
        value.type.isList() -> mapper.referenceMapListFunction()
        value.type.isSet() && value.type.isNullable() -> mapper.referenceMapNullableSetFunction()
        value.type.isSet() -> mapper.referenceMapSetFunction()
        value.type.isNullable() -> mapper.referenceMapNullableFunction()
        else -> mapper.referenceMapFunction()
    }

private fun IrClass.selectTransformationFunction(value: IrExpression) =
    when {
        value.type.isList() && value.type.isNullable() -> functions.first { it.isMappieMapNullableListFunction() }
        value.type.isList() -> functions.first { it.isMappieMapListFunction() }
        value.type.isSet() && value.type.isNullable() -> functions.first { it.isMappieMapNullableSetFunction() }
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
        panic("Class ${clazz.name.asString()} should either be an object or has an primary constructor without parameters.", clazz)
    }
