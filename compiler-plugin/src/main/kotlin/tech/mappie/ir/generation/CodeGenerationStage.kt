package tech.mappie.ir.generation

import org.jetbrains.kotlin.ir.declarations.IrClass
import tech.mappie.ir.MappieContext
import tech.mappie.ir.resolving.InternalMappieDefinition
import tech.mappie.ir.resolving.MappingRequest

object CodeGenerationStage {

    context(context: MappieContext)
    fun execute(mappings: Map<InternalMappieDefinition, MappingRequest>): CodeGenerationResult {
        val generated = mappings.map { (definition, mapping) ->
            val function = definition.referenceMapFunction()
            val model = CodeGenerationModelFactory.of(mapping).construct(function)
            definition.clazz.transform(MappieTranformer(context, model), null)
        }
        return CodeGenerationResult(generated.filterIsInstance<IrClass>())
    }
}


data class CodeGenerationResult(val classes: List<IrClass>)