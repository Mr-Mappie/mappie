package tech.mappie.resolving

import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.name.Name
import tech.mappie.resolving.classes.sources.ClassMappingSource
import tech.mappie.resolving.classes.targets.ClassMappingTarget
import tech.mappie.resolving.enums.EnumMappingTarget

sealed interface MappingRequest {
    val origin: IrFunction
    val target: IrType
}

class ClassMappingRequest(
    override val origin: IrFunction,
    val constructor: IrConstructor,
    val mappings : Map<ClassMappingTarget, List<ClassMappingSource>>,
    val unknowns: Map<Name, List<ClassMappingSource>>,
) : MappingRequest {
    override val target = constructor.returnType
}

class EnumMappingRequest(
    override val origin: IrFunction,
    val source: IrType,
    override val target: IrType,
    val mappings: Map<IrEnumEntry, List<EnumMappingTarget>>,
) : MappingRequest