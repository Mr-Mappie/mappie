package io.github.mappie.printing

import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.types.isNullable

fun IrType.pretty(): String =
    getClass()!!.name.asString() + if (isNullable()) "?" else ""