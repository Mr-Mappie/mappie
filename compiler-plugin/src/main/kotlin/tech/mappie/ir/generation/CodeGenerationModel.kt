package tech.mappie.ir.generation

import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.types.IrType
import tech.mappie.ir.GeneratedMappieDefinition
import tech.mappie.ir.InternalMappieDefinition
import tech.mappie.ir.MappieDefinition
import tech.mappie.ir.resolving.ClassMappings
import tech.mappie.ir.resolving.EnumMappings
import tech.mappie.ir.resolving.Mappings

sealed interface CodeGenerationModel {
    val definition: MappieDefinition
    val mappings: Mappings
}

data class EnumMappieCodeGenerationModel(
    override val definition: MappieDefinition,
    val source: IrType,
    val target: IrType,
    override val mappings: EnumMappings,
) : CodeGenerationModel

data class ClassMappieCodeGenerationModel(
    val origin: InternalMappieDefinition,
    override val definition: MappieDefinition,
    val constructor: IrConstructor,
    override val mappings: ClassMappings,
    val generated: Map<GeneratedMappieDefinition, CodeGenerationModel>,
) : CodeGenerationModel