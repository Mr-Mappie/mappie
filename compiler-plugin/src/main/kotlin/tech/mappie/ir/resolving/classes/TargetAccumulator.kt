package tech.mappie.ir.resolving.classes

import org.jetbrains.kotlin.name.Name
import tech.mappie.ir.resolving.classes.sources.ExplicitClassMappingSource

interface TargetAccumulator {
    fun explicit(entry: Pair<Name, ExplicitClassMappingSource>)
}