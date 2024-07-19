package tech.mappie.generation

import org.jetbrains.kotlin.ir.declarations.IrFunction
import tech.mappie.resolving.Mapping

class MappieGeneration(
    val mappings: Map<IrFunction, Mapping>,
)