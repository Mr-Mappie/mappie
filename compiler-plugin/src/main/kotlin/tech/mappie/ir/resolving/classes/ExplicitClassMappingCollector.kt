package tech.mappie.ir.resolving.classes

import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.Name
import tech.mappie.ir.util.IrBaseVisitor
import tech.mappie.api.ObjectMappie
import tech.mappie.ir.exceptions.MappiePanicException
import tech.mappie.ir.resolving.MappieDefinition
import tech.mappie.ir.resolving.ResolverContext
import tech.mappie.ir.resolving.classes.sources.*
import tech.mappie.util.*
import tech.mappie.ir.util.getterName

class ExplicitClassMappingCollector(private val context: ResolverContext)
    : IrBaseVisitor<ClassMappingRequestBuilder, ClassMappingRequestBuilder>() {
    override fun visitBlockBody(body: IrBlockBody, data: ClassMappingRequestBuilder) =
        body.statements.single().accept(data)

    override fun visitReturn(expression: IrReturn, data: ClassMappingRequestBuilder) =
        expression.value.accept(data)

    override fun visitCall(expression: IrCall, data: ClassMappingRequestBuilder) =
        when (expression.symbol.owner.name) {
            IDENTIFIER_MAPPING -> expression.valueArguments.first()!!.accept(data)
            else -> data
        }

    override fun visitFunctionExpression(expression: IrFunctionExpression, data: ClassMappingRequestBuilder) =
        data.apply {
            expression.function.body?.statements?.forEach { statement ->
                explicit(statement.accept(ClassMappingStatementCollector(context), Unit))
            }
        }
}

private class ClassMappingStatementCollector(private val context: ResolverContext)
    : IrBaseVisitor<Pair<Name, ExplicitClassMappingSource>, Unit>() {
    override fun visitCall(expression: IrCall, data: Unit) = when (expression.symbol.owner.name) {
        IDENTIFIER_FROM_PROPERTY, IDENTIFIER_FROM_PROPERTY_NOT_NULL -> {
            val target = expression.extensionReceiver!!.accept(TargetNameCollector(), Unit)
            target to ExplicitPropertyMappingSource(
                expression.valueArguments.first()!! as IrPropertyReference,
                null,
                expression.symbol.owner.name == IDENTIFIER_FROM_PROPERTY_NOT_NULL
            )
        }
        IDENTIFIER_FROM_VALUE -> {
            val target = expression.extensionReceiver!!.accept(TargetNameCollector(), Unit)
            target to ValueMappingSource(expression.valueArguments.first()!!)
        }
        IDENTIFIER_FROM_EXPRESSION -> {
            val target = expression.extensionReceiver!!.accept(TargetNameCollector(), data)
            target to ExpressionMappingSource(expression.valueArguments.first()!!)
        }
        IDENTIFIER_VIA -> {
            expression.dispatchReceiver!!.accept(data).let { (name, source) ->
                name to (source as ExplicitPropertyMappingSource).copy(
                    transformation = expression.valueArguments.first()!!.accept(MapperReferenceCollector(context), Unit)
                )
            }
        }
        IDENTIFIER_TRANSFORM -> {
            expression.dispatchReceiver!!.accept(data).let { (name, source) ->
                name to (source as ExplicitPropertyMappingSource).copy(
                    transformation = expression.valueArguments.first().let {
                        when (it) {
                            is IrFunctionExpression -> PropertyMappingTransformTranformation(it)
                            is IrFunctionReference -> PropertyMappingTransformTranformation(it)
                            else -> throw MappiePanicException("Unexpected expression type: ${expression.dumpKotlinLike()}")
                        }
                    }
                )
            }
        }
        else -> {
            throw MappiePanicException("Unexpected method call", expression)
        }
    }

    override fun visitTypeOperator(expression: IrTypeOperatorCall, data: Unit) =
        when (expression.operator.name) {
            "IMPLICIT_COERCION_TO_UNIT" -> expression.argument.accept(data)
            else -> super.visitTypeOperator(expression, data)
        }
}

private class MapperReferenceCollector(private val context: ResolverContext)
    : IrBaseVisitor<PropertyMappingViaMapperTransformation, Unit>() {

    override fun visitGetObjectValue(expression: IrGetObjectValue, data: Unit): PropertyMappingViaMapperTransformation {
        val mapper = context.pluginContext.referenceClass(expression.symbol.owner.classId!!)!!
        return PropertyMappingViaMapperTransformation(MappieDefinition(mapper.owner), expression)
    }

    override fun visitConstructorCall(expression: IrConstructorCall, data: Unit): PropertyMappingViaMapperTransformation {
        val mapper = expression.type.getClass()!!
        return PropertyMappingViaMapperTransformation(MappieDefinition(mapper), expression)
    }

    override fun visitCall(expression: IrCall, data: Unit): PropertyMappingViaMapperTransformation {
        require(expression.origin == IrStatementOrigin.GET_PROPERTY)

        return when (val name = expression.symbol.owner.name) {
            getterName(ObjectMappie<*, *>::forList.name) -> {
                val mapper = expression.symbol.owner.parent as IrClass
                PropertyMappingViaMapperTransformation(MappieDefinition(mapper), expression.dispatchReceiver!!)
            }
            getterName(ObjectMappie<*, *>::forSet.name) -> {
                val mapper = expression.symbol.owner.parent as IrClass
                PropertyMappingViaMapperTransformation(MappieDefinition(mapper), expression.dispatchReceiver!!)
            }
            else -> {
                throw MappiePanicException("Unexpected call of ${name.asString()}, expected forList or forSet", expression)
            }
        }
    }
}

private class TargetNameCollector : IrBaseVisitor<Name, Unit>() {

    override fun visitPropertyReference(expression: IrPropertyReference, data: Unit): Name {
        return expression.symbol.owner.name
    }

    @Suppress("UNCHECKED_CAST")
    override fun visitCall(expression: IrCall, data: Unit): Name {
        return when (expression.symbol.owner.name) {
            IDENTIFIER_TO -> {
                return Name.identifier((expression.valueArguments.first()!! as IrConst<String>).value)
            }
            else -> {
                super.visitCall(expression, data)
            }
        }
    }
}