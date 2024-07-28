package tech.mappie.resolving.classes

import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.name.Name

sealed interface GeneratedMappieClass {
    val name: Name
    val source: IrType
    val target: IrType
}

data class GeneratedMappieEnumClass(
    override val name: Name,
    override val source: IrType,
    val sourceEntries: List<IrEnumEntry>,
    override val target: IrType,
    val targetEntries: List<IrEnumEntry>,
) : GeneratedMappieClass
