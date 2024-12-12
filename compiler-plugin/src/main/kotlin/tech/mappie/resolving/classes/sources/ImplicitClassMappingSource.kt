package tech.mappie.resolving.classes.sources

import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.name.Name

sealed interface ImplicitClassMappingSource : ClassMappingSource

data class ParameterValueMappingSource(
    val parameter: Name,
    val parameterType: IrType,
    val transformation: PropertyMappingTransformation?,
) : ImplicitClassMappingSource {
    override val type = type(parameterType, transformation)

    override fun selectGeneratedTransformationMapping() =
        transformation as GeneratedViaMapperTransformation?
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

    override fun selectGeneratedTransformationMapping() =
        transformation as? GeneratedViaMapperTransformation?
}

data class FunctionMappingSource(
    val function: IrFunction,
    val parameter: Name,
    val parameterType: IrType,
    val transformation: PropertyMappingTransformation?,
) : ImplicitClassMappingSource {
    override val type = type(function.returnType, transformation)

    override fun selectGeneratedTransformationMapping() =
        transformation as? GeneratedViaMapperTransformation?
}
