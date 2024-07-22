package tech.mappie.generation

import org.jetbrains.kotlin.ir.declarations.IrFunction
import tech.mappie.resolving.Mapping
import tech.mappie.resolving.classes.MappieVia

class MappieGeneration(
    val mappings: Map<IrFunction, Mapping>,
    val generated: Set<MappieVia>,
)