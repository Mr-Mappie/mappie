package tech.mappie.generation

import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.types.IrType
import tech.mappie.resolving.classes.sources.ClassMappingSource
import tech.mappie.resolving.classes.targets.ClassMappingTarget
import tech.mappie.resolving.enums.EnumMappingTarget

sealed interface CodeGenerationModel {
    val declaration: IrFunction
}

data class EnumMappieCodeGenerationModel(
    override val declaration: IrFunction,
    val source: IrType,
    val target: IrType,
    val mappings: Map<IrEnumEntry, EnumMappingTarget>,
) : CodeGenerationModel

data class ClassMappieCodeGenerationModel(
    override val declaration: IrFunction,
    val constructor: IrConstructor,
    val mappings: Map<ClassMappingTarget, ClassMappingSource>,
) : CodeGenerationModel