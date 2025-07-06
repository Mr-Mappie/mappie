package tech.mappie.ir_old.resolving.enums

import tech.mappie.ir_old.util.BaseVisitor
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry

class EnumEntriesCollector : BaseVisitor<List<IrEnumEntry>, Unit>() {

    override fun visitClass(declaration: IrClass, data: Unit): List<IrEnumEntry> {
        return declaration.declarations
            .filterIsInstance<IrEnumEntry>()
            .flatMap { it.accept(data) }
    }

    override fun visitEnumEntry(declaration: IrEnumEntry, data: Unit): List<IrEnumEntry> {
        return listOf(declaration)
    }
}