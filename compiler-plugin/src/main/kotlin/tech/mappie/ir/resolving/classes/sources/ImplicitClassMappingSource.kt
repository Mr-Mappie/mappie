package tech.mappie.ir.resolving.classes.sources

import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.name.Name

sealed interface ImplicitClassMappingSource : ClassMappingSource

data class ParameterValueMappingSource(
    val parameter: Name,
    val parameterType: IrType,
    override val transformation: PropertyMappingTransformation?,
) : ImplicitClassMappingSource, TransformableClassMappingSource {
    override val type = type(parameterType)

    override fun toString() = "$parameter: ${parameterType.dumpKotlinLike()} via $transformation"
}

data class ParameterDefaultValueMappingSource(
    val parameter: IrValueParameter,
) : ImplicitClassMappingSource {
    override val type: IrType = parameter.type

    override fun toString() = "${parameter.name}: ${type.dumpKotlinLike()}"
}

data class ImplicitPropertyMappingSource(
    val property: IrProperty,
    val propertyType: IrType,
    val parameter: Name,
    val parameterType: IrType,
    override val transformation: PropertyMappingTransformation?,
) : ImplicitClassMappingSource, TransformableClassMappingSource {
    override val type = type(propertyType)

    override fun toString() = "$parameter.${property.name}: ${propertyType.dumpKotlinLike()} via $transformation"
}

data class FunctionMappingSource(
    val function: IrFunction,
    val functionType: IrType,
    val parameter: Name,
    val parameterType: IrType,
    override val transformation: PropertyMappingTransformation?,
) : ImplicitClassMappingSource, TransformableClassMappingSource {
    override val type = type(functionType)

    override fun toString() = "$parameter.${function.name}: ${functionType.dumpKotlinLike()} via $transformation"
}
