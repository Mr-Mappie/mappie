package tech.mappie.ir_old.generation

import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.util.*
import tech.mappie.exceptions.MappiePanicException.Companion.panic
import tech.mappie.referenceFunctionLet
import tech.mappie.ir_old.resolving.classes.sources.GeneratedViaMapperTransformation
import tech.mappie.ir_old.resolving.classes.sources.PropertyMappingTransformTransformation
import tech.mappie.ir_old.resolving.classes.sources.PropertyMappingTransformation
import tech.mappie.ir_old.resolving.classes.sources.PropertyMappingViaMapperTransformation
import tech.mappie.ir_old.util.*

fun IrBuilderWithScope.constructTransformation(context: CodeGenerationContext, transformation: PropertyMappingTransformation, value: IrExpression) =
    when (transformation) {
        is PropertyMappingTransformTransformation -> {
            irCall(context.referenceFunctionLet()).apply {
                arguments[0] = value
                arguments[1] = transformation.function
            }
        }
        is PropertyMappingViaMapperTransformation -> {
            irCall(transformation.selectTransformationFunction(value)).apply {
                arguments[0] = transformation.dispatchReceiver ?: instance(transformation.mapper.clazz)
                arguments[1] = value
            }
        }
        is GeneratedViaMapperTransformation -> {
            val clazz = context.generated[transformation.source.type.mappieType() to transformation.target.type.mappieType()]!!
            irCall(clazz.selectTransformationFunction(value)).apply {
                arguments[0] = instance(clazz)
                arguments[1] = value
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
        value.type.isList() && value.type.isNullable() ->
            listOf(this, superClass!!).firstNotNullOf { it.functions.firstOrNull { it.isMappieMapNullableListFunction() } }
        value.type.isList() ->
            listOf(this, superClass!!).firstNotNullOf { it.functions.firstOrNull { it.isMappieMapListFunction() } }
        value.type.isSet() && value.type.isNullable() ->
            listOf(this, superClass!!).firstNotNullOf { it.functions.firstOrNull { it.isMappieMapNullableSetFunction() } }
        value.type.isSet() ->
            listOf(this, superClass!!).firstNotNullOf { it.functions.firstOrNull { it.isMappieMapSetFunction() } }
        value.type.isNullable() ->
            listOf(this, superClass!!).firstNotNullOf { it.functions.firstOrNull { it.isMappieMapNullableFunction() } }
        else -> functions.first { it.isMappieMapFunction() }
    }

private fun IrBuilderWithScope.instance(clazz: IrClass) =
    if (clazz.isObject) {
        irGetObject(clazz.symbol)
    } else if (clazz.primaryConstructor != null && clazz.primaryConstructor!!.parameters.isEmpty()) {
        irCallConstructor(clazz.primaryConstructor!!.symbol, emptyList())
    } else {
        panic("Class ${clazz.name.asString()} should either be an object or has an primary constructor without parameters.", clazz)
    }
