package tech.mappie.ir.resolving

import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.types.IrType
import tech.mappie.ir.InternalMappieDefinition
import tech.mappie.ir.resolving.classes.sources.ClassMappingSource
import tech.mappie.ir.resolving.classes.targets.ClassMappingTarget
import tech.mappie.ir.resolving.enums.EnumMappingTarget

sealed interface MappingRequest {
    val origin: InternalMappieDefinition
    val source: IrType
    val target: IrType
}

class ClassMappingRequest(
    override val origin: InternalMappieDefinition,
    val sources: List<IrType>,
    val constructor: IrConstructor,
    val mappings: ClassMappings,
) : MappingRequest {
    override val source get() = sources.single()
    override val target = constructor.returnType
}

class EnumMappingRequest(
    override val origin: InternalMappieDefinition,
    override val source: IrType,
    override val target: IrType,
    val mappings: EnumMappings,
) : MappingRequest


sealed interface Mappings

sealed interface ClassMappings : Mappings

class TargetSourcesClassMappings(mappings: Map<ClassMappingTarget, List<ClassMappingSource>>)
    : ClassMappings, Map<ClassMappingTarget, List<ClassMappingSource>> by mappings

sealed interface EnumMappings : Mappings

class SourcesTargetEnumMappings(mappings: Map<IrEnumEntry, List<EnumMappingTarget>>)
    : EnumMappings, Map<IrEnumEntry, List<EnumMappingTarget>> by mappings

class SuperCallEnumMappings : EnumMappings