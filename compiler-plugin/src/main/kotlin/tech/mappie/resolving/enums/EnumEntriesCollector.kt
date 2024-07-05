package tech.mappie.resolving.enums

import org.jetbrains.kotlin.ir.IrFileEntry
import tech.mappie.BaseVisitor
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry

class EnumEntriesCollector(file: IrFileEntry) : BaseVisitor<List<IrEnumEntry>, Unit>(file) {

    override fun visitClass(declaration: IrClass, data: Unit): List<IrEnumEntry> {
        return declaration.declarations
            .filterIsInstance<IrEnumEntry>()
            .flatMap { it.accept(data) }
    }

    override fun visitEnumEntry(declaration: IrEnumEntry, data: Unit): List<IrEnumEntry> {
        return listOf(declaration)
    }
}