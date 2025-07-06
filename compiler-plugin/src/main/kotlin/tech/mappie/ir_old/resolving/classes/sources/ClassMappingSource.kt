package tech.mappie.ir_old.resolving.classes.sources

import org.jetbrains.kotlin.ir.types.*

sealed interface ClassMappingSource {
    val type: IrType
}