package tech.mappie.ir.preprocessing

import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import tech.mappie.ir.MappieContext
import tech.mappie.ir.MappieDefinitionCollection
import tech.mappie.ir.resolving.findMappingStatements

/**
 * IR stage responsible for gathering all internal- and external definitions of the module.
 */
object PreprocessingStage {

    context(context: MappieContext)
    fun execute(input: IrModuleFragment): PreprocessingResult {
        val definitions = DefinitionsCollector(context).collect(input)
        context.definitions.load(definitions)
        return PreprocessingResult(definitions.apply {
            internal.removeAll { definition ->
                val body = definition.referenceMapFunction().body
                body != null && findMappingStatements(body).isEmpty()
            }
        })
    }
}

data class PreprocessingResult(val definitions: MappieDefinitionCollection)