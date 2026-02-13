package tech.mappie.ir.preprocessing

import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import tech.mappie.ir.MappieContext
import tech.mappie.ir.MappieDefinitionCollection
import tech.mappie.ir.resolving.findMappingStatements

class DefinitionsCollector(val context: MappieContext) {

    context(context: MappieContext)
    fun collect(module: IrModuleFragment): MappieDefinitionCollection {
        val external = ExternalDefinitionsCollector(context).collect()
        val internal = module.accept(InternalDefinitionsCollector(context), Unit)
        val (internalShouldNotGenerate, internalShouldGenerate) = internal.partition {
            val body = it.referenceMapFunction().body
            body != null && findMappingStatements(body).isEmpty()
        }
        val incremental = IncrementalCompilationDefinitionsCollector().collect(internal)

        return MappieDefinitionCollection(
            internalShouldGenerate,
            internalShouldNotGenerate,
            incremental,
            external,
            mutableListOf()
        )
    }
}
