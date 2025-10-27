package tech.mappie.ir.generation

import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.types.IrType
import tech.mappie.ir.resolving.classes.sources.ClassMappingSource
import tech.mappie.ir.resolving.classes.targets.ClassMappingTarget
import tech.mappie.ir.resolving.enums.EnumMappingTarget

sealed interface CodeGenerationModel {
    val function: IrFunction
}

data class EnumMappieCodeGenerationModel(
    override val function: IrFunction,
    val source: IrType,
    val target: IrType,
    val mappings: Map<IrEnumEntry, EnumMappingTarget>,
) : CodeGenerationModel

data class ClassMappieCodeGenerationModel(
    override val function: IrFunction,
    val constructor: IrConstructor,
    val mappings: Map<ClassMappingTarget, ClassMappingSource>,
) : CodeGenerationModel