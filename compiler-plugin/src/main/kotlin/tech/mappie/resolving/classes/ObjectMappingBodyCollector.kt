package tech.mappie.resolving.classes

import tech.mappie.BaseVisitor
import tech.mappie.MappieIrRegistrar.Companion.context
import tech.mappie.api.ObjectMappie
import tech.mappie.resolving.*
import org.jetbrains.kotlin.ir.IrFileEntry
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.Name
import tech.mappie.mappieTerminate
import tech.mappie.util.*

class ObjectMappingBodyCollector(file: IrFileEntry)
    : BaseVisitor<ObjectMappingsConstructor, ObjectMappingsConstructor>(file) {

    override fun visitBlockBody(body: IrBlockBody, data: ObjectMappingsConstructor): ObjectMappingsConstructor {
        return body.statements.single().accept(data)
    }

    override fun visitReturn(expression: IrReturn, data: ObjectMappingsConstructor): ObjectMappingsConstructor {
        return expression.value.accept(data)
    }

    override fun visitCall(expression: IrCall, data: ObjectMappingsConstructor): ObjectMappingsConstructor {
        return when (expression.symbol.owner.name) {
            IDENTIFIER_MAPPING -> {
                expression.valueArguments.first()?.accept(data) ?: data
            }
            else -> {
                data
            }
        }
    }

    override fun visitFunctionExpression(expression: IrFunctionExpression, data: ObjectMappingsConstructor): ObjectMappingsConstructor {
        return expression.function.body?.statements?.fold(data) { acc, current ->
            acc.let { current.accept(ObjectBodyStatementCollector(file!!), Unit)?.let { acc.explicit(it) } ?: it }
        } ?: data
    }
}

private class ObjectBodyStatementCollector(file: IrFileEntry)
    : BaseVisitor<Pair<Name, ObjectMappingSource>?, Unit>(file) {

    override fun visitCall(expression: IrCall, data: Unit): Pair<Name, ObjectMappingSource>? {
        return when (expression.symbol.owner.name) {
            IDENTIFIER_FROM_PROPERTY -> {
                val target = expression.extensionReceiver!!.accept(TargetValueCollector(file!!), data)
                val source = expression.valueArguments.first()!!.accept(SourceValueCollector(file!!), Unit)

                target to source
            }
            IDENTIFIER_FROM_VALUE -> {
                val target = expression.extensionReceiver!!.accept(TargetValueCollector(file!!), data)
                val source = expression.valueArguments.first()!!

                target to ValueSource(source, expression)
            }
            IDENTIFIER_FROM_EXPRESSION -> {
                val target = expression.extensionReceiver!!.accept(TargetValueCollector(file!!), data)
                val source = expression.valueArguments.first() as IrFunctionExpression

                target to ExpressionSource(
                    source,
                    expression,
                )
            }
            IDENTIFIER_TRANSFORM -> {
                val mapping = expression.dispatchReceiver!!.accept(data)!!
                val transformation = MappieTransformTransformation(expression.valueArguments.first()!! as IrFunctionExpression)
                mapping.first to (mapping.second as PropertySource).copy(transformation = transformation)
            }
            IDENTIFIER_VIA -> {
                val mapping = expression.dispatchReceiver!!.accept(data)!!
                val transformation = expression.valueArguments.first()!!.accept(MapperReferenceCollector(file!!), Unit)
                mapping.first to (mapping.second as PropertySource).copy(
                    transformation = transformation
                )
            }
            else -> {
                logError("Unexpected method call", file?.let { location(it, expression) })
                return null
            }
        }
    }

    override fun visitTypeOperator(expression: IrTypeOperatorCall, data: Unit): Pair<Name, ObjectMappingSource>? {
        return when (expression.operator.name) {
            "IMPLICIT_COERCION_TO_UNIT" -> expression.argument.accept(data)
            else -> super.visitTypeOperator(expression, data)
        }
    }

    override fun visitReturn(expression: IrReturn, data: Unit): Pair<Name, ObjectMappingSource>? {
        return expression.value.accept(data)
    }

    override fun visitGetObjectValue(expression: IrGetObjectValue, data: Unit): Pair<Name, ObjectMappingSource>? {
        return null
    }
}

private class MapperReferenceCollector(file: IrFileEntry) : BaseVisitor<MappieTransformation, Unit>(file) {

    override fun visitGetObjectValue(expression: IrGetObjectValue, data: Unit): MappieTransformation {
        val function = context.referenceClass(expression.symbol.owner.classId!!)!!
            .functions
            .filter { it.owner.isMappieMapFunction() }
            .first()

        return MappieViaTransformation(function.owner, expression)
    }

    override fun visitConstructorCall(expression: IrConstructorCall, data: Unit): MappieTransformation {
        val function = expression.type.getClass()!!.functions
            .filter { it.isMappieMapFunction() }
            .first()

        return MappieViaTransformation(function, expression)
    }

    override fun visitCall(expression: IrCall, data: Unit): MappieTransformation {
        require(expression.origin == IrStatementOrigin.GET_PROPERTY)

        return when (val name = expression.symbol.owner.name) {
            getterName(ObjectMappie<*, *>::forList.name) -> {
                val mapper = expression.symbol.owner.parent as IrClass

                val function = mapper.functions
                    .filter { it.isMappieMapListFunction() }
                    .first()

                MappieViaTransformation(function, expression.dispatchReceiver!!)
            }

            getterName(ObjectMappie<*, *>::forSet.name) -> {
                val mapper = expression.symbol.owner.parent as IrClass

                val function = mapper.functions
                    .filter { it.isMappieMapSetFunction() }
                    .first()

                MappieViaTransformation(function, expression.dispatchReceiver!!)
            }

            else -> {
                mappieTerminate(
                    "Unexpected call of ${name.asString()}, expected forList or forSet",
                    file?.let { location(it, expression) })
            }
        }
    }
}

private class SourceValueCollector(file: IrFileEntry) : BaseVisitor<ObjectMappingSource, Unit>(file) {

    override fun visitPropertyReference(expression: IrPropertyReference, data: Unit): ObjectMappingSource {
        return PropertySource(
            property = expression,
            transformation = null,
            origin = expression,
        )
    }

    override fun visitConst(expression: IrConst<*>, data: Unit): ObjectMappingSource {
        return ValueSource(expression, expression)
    }
}

private class TargetValueCollector(file: IrFileEntry) : BaseVisitor<Name, Unit>(file) {

    override fun visitPropertyReference(expression: IrPropertyReference, data: Unit): Name {
        return expression.symbol.owner.name
    }

    override fun visitCall(expression: IrCall, data: Unit): Name {
        return when (expression.symbol.owner.name) {
            IDENTIFIER_PARAMETER -> {
                val value = expression.valueArguments.first()!!
                return if (value.isConstantLike && value is IrConst<*>) {
                    Name.identifier(value.value as String)
                } else {
                    mappieTerminate("Parameter name must be a constant", location(file!!, expression))
                }
            }
            else -> {
                super.visitCall(expression, data)
            }
        }
    }
}