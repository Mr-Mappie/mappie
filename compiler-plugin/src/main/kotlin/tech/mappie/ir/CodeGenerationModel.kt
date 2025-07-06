package tech.mappie.ir

import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrValueDeclaration
import org.jetbrains.kotlin.ir.declarations.IrValueParameter

sealed interface CodeGenerationModel

data class ValueParameterTarget(val parameter: IrValueParameter)

data class PropertySource(val receiver: IrValueDeclaration, val property: IrProperty)

data class UserDefinedClassCodeGenerationModel(
    val constructor: IrConstructor,
    val mappings: Map<ValueParameterTarget, PropertySource>,
) : CodeGenerationModel
