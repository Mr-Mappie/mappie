package tech.mappie.ir.generation

import org.jetbrains.kotlin.ir.declarations.IrClass
import tech.mappie.ir.GeneratedMappieDefinition
import tech.mappie.ir.MappieContext
import tech.mappie.ir.MappieDefinition
import tech.mappie.ir.resolving.classes.sources.PropertyMappingViaMapperTransformation
import tech.mappie.ir.resolving.classes.sources.TransformableClassMappingSource

object CodeGenerationStage {

    context(context: MappieContext)
    fun execute(mappings: Map<MappieDefinition, CodeGenerationModel>): CodeGenerationResult {
        val elements = mappings.toList().map { (definition, model) ->
            if (model is ClassMappieCodeGenerationModel) {
                model.generated.forEach {
                    val generated = GeneratedMappieClassConstructor()
                        .construct(definition.clazz, it.key, it.value)

                    val model = it.value.substitute(it.key.clazz as IrLazyGeneratedClass, generated)
                    execute(mapOf(generated to model))
                }
            }

            definition.clazz.transform(MappieTranformer(context, model), null)
        }

        return CodeGenerationResult(elements.filterIsInstance<IrClass>())
    }

    fun CodeGenerationModel.substitute(original: IrLazyGeneratedClass, concrete: GeneratedMappieDefinition): CodeGenerationModel {
        return when (this) {
            is ClassMappieCodeGenerationModel -> {
                ClassMappieCodeGenerationModel(
                    origin,
                    concrete,
                    constructor,
                    mappings.mapValues { (_, source) ->
                        if (source is TransformableClassMappingSource) {
                            val transformation = source.transformation
                            if (transformation is PropertyMappingViaMapperTransformation) {
                                if (transformation.mapper.clazz == original) {
                                    source.clone(transformation = transformation.copy(mapper = concrete))
                                } else {
                                    source
                                }
                            } else {
                                source
                            }
                        } else {
                            source
                        }
                    },
                    generated,
                )
            }
            is EnumMappieCodeGenerationModel -> {
                copy(definition = concrete)
            }
        }
    }
}

data class CodeGenerationResult(val classes: List<IrClass>)