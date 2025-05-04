package tech.mappie.ir.resolving

import org.jetbrains.kotlin.ir.declarations.IrConstructor
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.name.Name
import tech.mappie.ir.resolving.classes.sources.ClassMappingSource
import tech.mappie.ir.resolving.classes.targets.ClassMappingTarget
import tech.mappie.ir.resolving.enums.EnumMappingTarget

sealed interface MappingRequest {
    val origin: IrFunction
    val source: IrType
    val target: IrType
}

sealed interface ClassRequest : MappingRequest {
    val mappings: Map<ClassMappingTarget, List<ClassMappingSource>>
}

class ClassMappingRequest(
    override val origin: IrFunction,
    val sources: List<IrType>,
    val constructor: IrConstructor,
    override val mappings : Map<ClassMappingTarget, List<ClassMappingSource>>,
    val unknowns: Map<Name, List<ClassMappingSource>>,
) : ClassRequest {
    override val source get() = sources.single()
    override val target = constructor.returnType
}
data class ClassUpdateRequest(
    override val origin: IrFunction,
    override val source: IrType,
    val name: Name,
    override val mappings: Map<ClassMappingTarget, List<ClassMappingSource>>,
): ClassRequest {
    override val target = source
}

class EnumMappingRequest(
    override val origin: IrFunction,
    override val source: IrType,
    override val target: IrType,
    val mappings: Map<IrEnumEntry, List<EnumMappingTarget>>,
) : MappingRequest
