package tech.mappie.resolving.classes

import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.Name
import tech.mappie.util.BaseVisitor
import tech.mappie.api.ObjectMappie
import tech.mappie.exceptions.MappiePanicException
import tech.mappie.resolving.MappieDefinition
import tech.mappie.resolving.ResolverContext
import tech.mappie.resolving.classes.sources.*
import tech.mappie.util.*
import tech.mappie.validation.Problem

class ExplicitClassMappingCollector(private val context: ResolverContext)
    : BaseVisitor<ClassMappingRequestBuilder, ClassMappingRequestBuilder>() {
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
    : BaseVisitor<Pair<Name, ExplicitClassMappingSource>, Unit>() {
    override fun visitCall(expression: IrCall, data: Unit) = when (expression.symbol.owner.name) {
        IDENTIFIER_FROM_PROPERTY -> {
            val target = expression.extensionReceiver!!.accept(TargetNameCollector(context), Unit)
            target to ExplicitPropertyMappingSource(expression.valueArguments.first()!! as IrPropertyReference, null)
        }
        IDENTIFIER_FROM_VALUE -> {
            val target = expression.extensionReceiver!!.accept(TargetNameCollector(context), Unit)
            target to ValueMappingSource(expression.valueArguments.first()!!)
        }
        IDENTIFIER_FROM_EXPRESSION -> {
            val target = expression.extensionReceiver!!.accept(TargetNameCollector(context), data)
            target to ExpressionMappingSource(expression.valueArguments.first() as IrFunctionExpression)
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
                    transformation = PropertyMappingTransformTranformation(expression.valueArguments.first()!! as IrFunctionExpression)
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
    : BaseVisitor<PropertyMappingViaMapperTransformation, Unit>() {

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

private class TargetNameCollector(private val context: ResolverContext) : BaseVisitor<Name, Unit>() {

    override fun visitPropertyReference(expression: IrPropertyReference, data: Unit): Name {
        return expression.symbol.owner.name
    }

    override fun visitCall(expression: IrCall, data: Unit): Name {
        return when (expression.symbol.owner.name) {
            IDENTIFIER_TO -> {
                val value = expression.valueArguments.first()!!
                return if (value.isConstantLike && value is IrConst<*>) {
                    Name.identifier(value.value as String)
                } else {
                    val problem = Problem.error(
                        "Identifier must be a compile-time constant",
                        location(context.function!!.fileEntry, expression)
                    )
                    context.logger.log(problem)
                    throw MappiePanicException("Identifier must be a compile-time constant", expression)
                }
            }
            else -> {
                super.visitCall(expression, data)
            }
        }
    }
}