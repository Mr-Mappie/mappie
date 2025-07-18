package tech.mappie.ir

import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrValueDeclaration
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.types.IrType

sealed interface CodeGenerationModel

sealed interface IrClassTarget

data class IrValueParameterClassTarget(val parameter: IrValueParameter) : IrClassTarget

sealed interface IrClassSource

class IrExplicitPropertyClassSource : IrClassSource

data class IrImplicitPropertyClassSource(val receiver: IrValueDeclaration, val property: IrProperty) : IrClassSource

data class UserDefinedClassCodeGenerationModel(
    val constructor: IrConstructor,
    val mappings: Map<IrValueParameterClassTarget, IrClassSource>,
) : CodeGenerationModel

sealed interface IrEnumTarget

data class IrEnumEntryTarget(val entry: IrEnumEntry) : IrEnumTarget

data class UserDefinedEnumCodeGenerationModel(
    val parameter: IrValueParameter,
    val target: IrType,
    val mappings: Map<IrEnumTarget, IrEnumEntry>
) : CodeGenerationModel
