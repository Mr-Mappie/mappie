package tech.mappie.ir.exceptions

import org.jetbrains.kotlin.ir.IrElement

class MappiePanicException(message: String, val origin: IrElement? = null) : Exception(message)