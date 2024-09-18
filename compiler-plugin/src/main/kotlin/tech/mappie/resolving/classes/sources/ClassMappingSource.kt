package tech.mappie.resolving.classes.sources

import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.expressions.IrPropertyReference
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.name.Name
import tech.mappie.MappieIrRegistrar.Companion.context
import tech.mappie.resolving.MappieDefinition
import tech.mappie.resolving.classes.targets.ClassMappingTarget
import tech.mappie.util.isList
import tech.mappie.util.isSet

sealed interface ClassMappingSource {
    val type: IrType
}

sealed interface ImplicitClassMappingSource : ClassMappingSource
sealed interface ExplicitClassMappingSource : ClassMappingSource

data class ParameterDefaultValueMappingSource(
    val parameter: IrValueParameter,
) : ImplicitClassMappingSource {
    override val type: IrType = parameter.type
}

data class ImplicitPropertyMappingSource(
    val property: IrProperty,
    val parameter: Name,
    val transformation: PropertyMappingTransformation?,
) : ImplicitClassMappingSource {
    override val type = type(property.getter!!.returnType, transformation)
}

data class FunctionMappingSource(
    val function: IrFunction,
    val parameter: Name,
) : ImplicitClassMappingSource {
    override val type = function.returnType
}

data class ExplicitPropertyMappingSource(
    val reference: IrPropertyReference,
    val transformation : PropertyMappingTransformation?,
) : ExplicitClassMappingSource {
    override val type = type(reference.getter!!.owner.returnType, transformation)
}

data class ValueMappingSource(val expression: IrExpression) : ExplicitClassMappingSource {
    override val type = expression.type
}

data class ExpressionMappingSource(val expression: IrExpression) : ExplicitClassMappingSource {
    override val type = (expression.type as IrSimpleType).arguments[1].typeOrFail
}

sealed interface PropertyMappingTransformation {
    val type: IrType
}

data class PropertyMappingTransformTranformation(
    val function: IrFunctionExpression,
) : PropertyMappingTransformation {
    override val type = function.function.returnType
}

data class PropertyMappingViaMapperTransformation(
    val mapper: MappieDefinition,
    val dispatchReceiver: IrExpression?,
) : PropertyMappingTransformation {
    override val type = mapper.target
}

data class GeneratedViaMapperTransformation(
    val source: ClassMappingSource,
    val target: ClassMappingTarget,
) : PropertyMappingTransformation {
    override val type = target.type
}

private fun type(original: IrType, transformation: PropertyMappingTransformation?): IrType {
    return if (transformation == null) {
        original
    } else {
        when (transformation) {
            is PropertyMappingViaMapperTransformation, is GeneratedViaMapperTransformation -> {
                when {
                    original.isSet() -> context.irBuiltIns.setClass.typeWith(transformation.type)
                    original.isList() -> context.irBuiltIns.listClass.typeWith(transformation.type)
                    else -> transformation.type
                }.run { if (original.isNullable()) makeNullable() else this }.addAnnotations(original.annotations)
            }
            is PropertyMappingTransformTranformation -> {
                transformation.type
            }
        }
    }
}
