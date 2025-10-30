package tech.mappie.ir.generation

import org.jetbrains.kotlin.ir.declarations.IrClass
import tech.mappie.ir.MappieContext
import tech.mappie.ir.MappieDefinition

object CodeGenerationStage {

    context(context: MappieContext)
    fun execute(mappings: Map<MappieDefinition, CodeGenerationModel>): CodeGenerationResult {
        val elements = mappings.toList().map { (definition, model) ->
            if (model is ClassMappieCodeGenerationModel) {
                model.generated.map {
                    val definition = GeneratedMappieClassConstructor()
                        .construct(definition.clazz, it.key, it.value)

                    definition.clazz.transform(MappieTranformer(context, it.value.clone(definition = definition)), null)
                }
            }

            definition.clazz.transform(MappieTranformer(context, model), null)
        }

        return CodeGenerationResult(elements.filterIsInstance<IrClass>())
    }
}

data class CodeGenerationResult(val classes: List<IrClass>)