package tech.mappie.ir.generation

import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.name.Name
import tech.mappie.ir.resolving.classes.sources.ClassMappingSource
import tech.mappie.ir.resolving.classes.targets.ClassMappingTarget
import tech.mappie.ir.resolving.enums.EnumMappingTarget

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

data class ClassUpdateCodeGenerationModel(
    override val declaration: IrFunction,
    val source: Name,
    val mappings: Map<ClassMappingTarget, ClassMappingSource>,
) : CodeGenerationModel