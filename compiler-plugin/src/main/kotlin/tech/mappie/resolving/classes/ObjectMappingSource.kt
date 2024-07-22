package tech.mappie.resolving.classes

import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.expressions.IrPropertyReference
import org.jetbrains.kotlin.ir.types.*
import tech.mappie.resolving.classes.sources.MappieSource

sealed interface ObjectMappingSource {
    val type: IrType
}

data class ResolvedSource(
    val property: MappieSource,
    val via: MappieVia? = null,
    val viaType: IrType? = null,
) : ObjectMappingSource {
    override val type: IrType get() = viaType ?: property.type
}

data class PropertySource(
    val property: IrPropertyReference,
    val transformation: MappieTransformation? = null,
    val origin: IrExpression,
) : ObjectMappingSource {

    val getter = property.getter!!

    override val type: IrType get() = when (transformation) {
        is MappieTransformTransformation -> (transformation.type as IrSimpleType).arguments[1].typeOrFail
        is MappieViaTransformation -> if (getter.owner.returnType.isNullable()) transformation.type.makeNullable() else transformation.type
        null -> getter.owner.returnType
    }
}

data class ExpressionSource(
    val expression: IrFunctionExpression,
    val origin: IrExpression,
) : ObjectMappingSource {
    override val type = expression.function.returnType
}

data class ValueSource(
    val value: IrExpression,
    val origin: IrExpression?,
) : ObjectMappingSource {
    override val type = value.type
}

class DefaultArgumentSource(override val type: IrType) : ObjectMappingSource