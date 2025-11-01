package tech.mappie.ir.resolving.classes

import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classOrFail
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.Name
import tech.mappie.ir.MappieContext
import tech.mappie.ir.util.BaseVisitor
import tech.mappie.exceptions.MappiePanicException.Companion.panic
import tech.mappie.exceptions.MappieProblemException.Companion.fail
import tech.mappie.ir.InternalMappieDefinition
import tech.mappie.ir.resolving.classes.sources.*
import tech.mappie.ir.util.isMappieMapFunction
import tech.mappie.ir.util.location
import tech.mappie.util.*

class ClassMappingStatementCollector : BaseVisitor<Pair<Name, ExplicitClassMappingSource>?, MappieContext>() {
    override fun visitCall(expression: IrCall, data: MappieContext) = when (expression.symbol.owner.name) {
        IDENTIFIER_FROM_PROPERTY, IDENTIFIER_FROM_PROPERTY_NOT_NULL -> {
            val target = expression.arguments[1]!!.accept(TargetNameCollector(), data)
            target to ExplicitPropertyMappingSource(
                expression.arguments[2]!! as IrPropertyReference,
                null,
                expression.symbol.owner.name == IDENTIFIER_FROM_PROPERTY_NOT_NULL
            )
        }
        IDENTIFIER_FROM_VALUE -> {
            val target = expression.arguments[1]!!.accept(TargetNameCollector(), data)
            target to ValueMappingSource(expression.arguments[2]!!)
        }
        IDENTIFIER_FROM_EXPRESSION -> {
            val target = expression.arguments[1]!!.accept(TargetNameCollector(), data)
            target to ExpressionMappingSource(expression.arguments[2]!!)
        }
        IDENTIFIER_VIA -> {
            expression.dispatchReceiver!!.accept(data)!!.let { (name, source) ->
//                val constructorCall = expression.arguments[1]!! as IrConstructorCall
//                val mapperClass = constructorCall.type.classOrFail
//                val typeArguments = if (constructorCall.typeArguments.isNotEmpty()) {
//                    listOf(constructorCall.typeArguments.last()!!)
//                } else {
//                    emptyList()
//                }
//
//                val target = mapperClass.functions
//                    .map { it.owner }
//                    .first { it.isMappieMapFunction() }
//                    .returnType
//                    .classOrFail
//                    .typeWith(typeArguments)

//                val target = expression.arguments[1]!!.accept(ClassMappingTargetTypeResolver(), Unit)

                name to (source as ExplicitPropertyMappingSource).copy(
                    transformation = expression.arguments[1]!!.accept(MapperReferenceCollector(), data)
                )
            }
        }
        IDENTIFIER_TRANSFORM -> {
            expression.dispatchReceiver!!.accept(data)!!.let { (name, source) ->
                name to (source as ExplicitPropertyMappingSource).copy(
                    transformation = expression.arguments[1]!!.let {
                        when (it) {
                            is IrFunctionExpression -> PropertyMappingTransformTransformation(it)
                            is IrFunctionReference -> PropertyMappingTransformTransformation(it)
                            else -> panic("Unexpected expression type: ${expression.dumpKotlinLike()}", expression)
                        }
                    }
                )
            }
        }
        else -> {
            super.visitCall(expression, data)
        }
    }

    override fun visitTypeOperator(expression: IrTypeOperatorCall, data: MappieContext) =
        when (expression.operator.name) {
            "IMPLICIT_COERCION_TO_UNIT" -> expression.argument.accept(data)
            else -> super.visitTypeOperator(expression, data)
        }

    override fun visitReturn(expression: IrReturn, data: MappieContext): Pair<Name, ExplicitClassMappingSource>? =
        null
}

private class MapperReferenceCollector : BaseVisitor<PropertyMappingViaMapperTransformation, MappieContext>() {

    override fun visitGetObjectValue(expression: IrGetObjectValue, data: MappieContext): PropertyMappingViaMapperTransformation {
        val mapper = data.pluginContext.referenceClass(expression.symbol.owner.classId!!)!!.owner

        val target = mapper.functions
            .first { it.isMappieMapFunction() }
            .returnType
            .classOrFail
            .typeWith(emptyList())

        return context(data) {
            PropertyMappingViaMapperTransformation(InternalMappieDefinition.of(mapper), expression, target)
        }
    }

    override fun visitConstructorCall(expression: IrConstructorCall, data: MappieContext): PropertyMappingViaMapperTransformation {
        val mapper = expression.type.getClass()!!
        val typeArguments = if (expression.typeArguments.isNotEmpty()) {
            listOf(expression.typeArguments.last()!!)
        } else {
            emptyList()
        }

        val target = mapper.functions
            .first { it.isMappieMapFunction() }
            .returnType
            .classOrFail
            .typeWith(typeArguments)

        return context(data) {
            PropertyMappingViaMapperTransformation(InternalMappieDefinition.of(mapper), expression, target)
        }
    }
}

private class TargetNameCollector : BaseVisitor<Name, MappieContext>() {

    override fun visitPropertyReference(expression: IrPropertyReference, data: MappieContext): Name {
        return expression.symbol.owner.name
    }

    override fun visitCall(expression: IrCall, data: MappieContext): Name {
        return when (expression.symbol.owner.name) {
            IDENTIFIER_TO -> {
                val value = expression.arguments[1]!!
                return if (value.isConstantLike && value is IrConst) {
                    Name.identifier(value.value as String)
                } else {
                    data.fail(
                        "Identifier must be a compile-time constant",
                        expression,
                        location(TODO(), expression)
                    )
                }
            }
            else -> {
                super.visitCall(expression, data)
            }
        }
    }
}