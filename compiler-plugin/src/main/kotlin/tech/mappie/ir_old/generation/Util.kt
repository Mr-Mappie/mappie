package tech.mappie.ir_old.generation

import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.declarations.IrSimpleFunction
import org.jetbrains.kotlin.ir.util.parentAsClass
import org.jetbrains.kotlin.name.Name

fun IrEnumEntry.referenceFunctionValueOf(): IrSimpleFunction =
    parentAsClass.declarations
        .filterIsInstance<IrSimpleFunction>()
        .single { it.name == Name.identifier("valueOf") }

