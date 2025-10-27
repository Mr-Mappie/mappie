package tech.mappie.ir.preprocessing

import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import tech.mappie.ir.MappieContext
import tech.mappie.ir.resolving.MappieDefinitionCollection

class DefinitionsCollector(val context: MappieContext) {
    fun collect(module: IrModuleFragment): MappieDefinitionCollection {
        val external = ExternalDefinitionsCollector(context).collect()
        val internal = module.accept(InternalDefinitionsCollector(context), Unit)
        return MappieDefinitionCollection(internal.toMutableList(), external.toMutableList())
    }
}
