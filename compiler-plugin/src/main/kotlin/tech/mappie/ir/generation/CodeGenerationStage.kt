package tech.mappie.ir.generation

import org.jetbrains.kotlin.ir.declarations.IrClass
import tech.mappie.ir.MappieContext
import tech.mappie.ir.MappieDefinition

object CodeGenerationStage {

    context(context: MappieContext)
    fun execute(mappings: Map<MappieDefinition, CodeGenerationModel>): CodeGenerationResult {
        val elements = mappings.toList().map { (definition, model) ->
            if (model is ClassMappieCodeGenerationModel) {
                model.generated.forEach {
                    val generated = GeneratedMappieClassConstructor()
                        .construct(definition.clazz, it.key, it.value)

                    val model = it.value.clone(definition = generated)
                    execute(mapOf(generated to model))
                }
            }

            definition.clazz.transform(MappieTranformer(context, model), null)
        }

        return CodeGenerationResult(elements.filterIsInstance<IrClass>())
    }
}

data class CodeGenerationResult(val classes: List<IrClass>)