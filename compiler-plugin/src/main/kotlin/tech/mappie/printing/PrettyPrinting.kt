package tech.mappie.printing

import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.types.isNullable

fun IrType.pretty() =
    getClass()!!.name.asString() + if (isNullable()) "?" else ""