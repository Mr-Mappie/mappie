package tech.mappie.validation

import org.jetbrains.kotlin.ir.declarations.IrFunction
import tech.mappie.MappieContext
import tech.mappie.resolving.MappieDefinition

class ValidationContext(
    context: MappieContext,
    val definitions: List<MappieDefinition>,
    val function: IrFunction,
) : MappieContext by context