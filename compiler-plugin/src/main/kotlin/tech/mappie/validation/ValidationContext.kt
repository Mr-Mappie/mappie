package tech.mappie.validation

import org.jetbrains.kotlin.ir.IrFileEntry
import tech.mappie.MappieContext

class ValidationContext(context: MappieContext, val file: IrFileEntry) : MappieContext by context