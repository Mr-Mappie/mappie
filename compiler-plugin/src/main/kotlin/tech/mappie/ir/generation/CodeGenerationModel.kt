package tech.mappie.ir.generation

import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.types.IrType
import tech.mappie.ir.GeneratedMappieDefinition
import tech.mappie.ir.InternalMappieDefinition
import tech.mappie.ir.MappieDefinition
import tech.mappie.ir.resolving.classes.sources.ClassMappingSource
import tech.mappie.ir.resolving.classes.targets.ClassMappingTarget
import tech.mappie.ir.resolving.enums.EnumMappingTarget

sealed interface CodeGenerationModel {
    val definition: MappieDefinition

    fun clone(definition: MappieDefinition): CodeGenerationModel = when(this) {
        is ClassMappieCodeGenerationModel -> this.copy(definition = definition)
        is EnumMappieCodeGenerationModel -> this.copy(definition = definition)
    }
}

data class EnumMappieCodeGenerationModel(
    override val definition: MappieDefinition,
    val source: IrType,
    val target: IrType,
    val mappings: Map<IrEnumEntry, EnumMappingTarget>,
) : CodeGenerationModel

data class ClassMappieCodeGenerationModel(
    val origin: InternalMappieDefinition,
    override val definition: MappieDefinition,
    val constructor: IrConstructor,
    val mappings: Map<ClassMappingTarget, ClassMappingSource>,
    val generated: Map<GeneratedMappieDefinition, CodeGenerationModel>,
) : CodeGenerationModel