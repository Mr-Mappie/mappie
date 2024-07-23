package tech.mappie.resolving.classes

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.name.Name

sealed interface MappieVia

data class MappieViaGeneratedEnumClass(
    val name: Name,
    val source: IrType,
    val sourceEntries: List<IrEnumEntry>,
    val target: IrType,
    val targetEntries: List<IrEnumEntry>,
) : MappieVia

data class MappieViaClass(val clazz: IrClass)
    : MappieVia