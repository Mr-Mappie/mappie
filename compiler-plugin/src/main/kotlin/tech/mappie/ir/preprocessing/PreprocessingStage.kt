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

        // Collect local conversion methods for each internal definition
        val localConversionCollector = LocalConversionMethodCollector(context)
        definitions.internal.forEach { definition ->
            val localConversions = localConversionCollector.collect(definition.clazz)
            if (localConversions.isNotEmpty()) {
                context.localConversions[definition.clazz] = localConversions
            }
        }

        return PreprocessingResult(definitions.apply {
            internal.removeAll { definition ->
                val body = definition.referenceMapFunction().body
                body != null && findMappingStatements(body).isEmpty()
            }
        })
    }
}

data class PreprocessingResult(val definitions: MappieDefinitionCollection)