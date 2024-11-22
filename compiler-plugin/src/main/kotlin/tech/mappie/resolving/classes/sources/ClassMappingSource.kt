package tech.mappie.resolving.classes.sources

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionExpression
import org.jetbrains.kotlin.ir.expressions.IrFunctionReference
import org.jetbrains.kotlin.ir.expressions.IrPropertyReference
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.name.Name
import tech.mappie.MappieIrRegistrar.Companion.context
import tech.mappie.exceptions.MappiePanicException
import tech.mappie.resolving.MappieDefinition
import tech.mappie.resolving.classes.targets.ClassMappingTarget
import tech.mappie.util.isList
import tech.mappie.util.isSet
import tech.mappie.util.mappieType

sealed interface ClassMappingSource {
    val type: IrType

    fun hasGeneratedTransformationMapping() =
        (this is ImplicitPropertyMappingSource && transformation is GeneratedViaMapperTransformation) ||
                (this is ExplicitPropertyMappingSource && transformation is GeneratedViaMapperTransformation)

    fun selectGeneratedTransformationMapping() =
        when (this) {
            is ExplicitPropertyMappingSource -> transformation as GeneratedViaMapperTransformation
            is ImplicitPropertyMappingSource ->  transformation as GeneratedViaMapperTransformation
            else -> throw MappiePanicException("source $this should not occur in selectGeneratedTransformationMapping.")
        }
}

sealed interface ImplicitClassMappingSource : ClassMappingSource
sealed interface ExplicitClassMappingSource : ClassMappingSource {
    val origin: IrElement
}

data class ParameterDefaultValueMappingSource(
    val parameter: IrValueParameter,
) : ImplicitClassMappingSource {
    override val type: IrType = parameter.type
}

data class ImplicitPropertyMappingSource(
    val property: IrProperty,
    val parameter: Name,
    val parameterType: IrType,
    val transformation: PropertyMappingTransformation?,
) : ImplicitClassMappingSource {
    override val type = type(property.getter!!.returnType, transformation)
}

data class FunctionMappingSource(
    val function: IrFunction,
    val parameter: Name,
    val parameterType: IrType,
    val transformation: PropertyMappingTransformation?,
) : ImplicitClassMappingSource {
    override val type = type(function.returnType, transformation)
}

data class ExplicitPropertyMappingSource(
    val reference: IrPropertyReference,
    val transformation : PropertyMappingTransformation?,
    val forceNonNull: Boolean,
) : ExplicitClassMappingSource {
    val getterType =  reference.getter!!.owner.returnType
    override val type = type(getterType.let { if (forceNonNull) it.makeNotNull() else it }, transformation)
    override val origin = reference
}

data class ValueMappingSource(val expression: IrExpression) : ExplicitClassMappingSource {
    override val type = expression.type
    override val origin = expression
}

data class ExpressionMappingSource(val expression: IrExpression) : ExplicitClassMappingSource {
    override val type = (expression.type as IrSimpleType).arguments[1].typeOrFail
    override val origin = expression
}

sealed interface PropertyMappingTransformation {
    val type: IrType
}

data class PropertyMappingTransformTranformation private constructor(
    val function: IrExpression,
    override val type: IrType,
) : PropertyMappingTransformation {
    constructor(functionReference: IrFunctionReference) : this(functionReference, functionReference.symbol.owner.returnType)
    constructor(functionExpression: IrFunctionExpression) : this(functionExpression, functionExpression.function.returnType)
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
    override val type = target.type.mappieType()
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
