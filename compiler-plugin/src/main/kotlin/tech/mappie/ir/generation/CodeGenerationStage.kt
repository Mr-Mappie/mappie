package tech.mappie.ir.generation

import org.jetbrains.kotlin.ir.declarations.IrClass
import tech.mappie.ir.GeneratedMappieDefinition
import tech.mappie.ir.MappieContext
import tech.mappie.ir.MappieDefinition
import tech.mappie.ir.resolving.ClassMappings
import tech.mappie.ir.resolving.TargetSourcesClassMappings
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

                    val model = it.value.replaceTranformationStubs(it.key.clazz as IrMappieGeneratedClass, generated)
                    execute(mapOf(generated to model))
                }
            }

            definition.clazz.transform(MappieTransformer(context, model), null)
        }

        return CodeGenerationResult(elements.filterIsInstance<IrClass>())
    }

    private fun CodeGenerationModel.replaceTranformationStubs(original: IrMappieGeneratedClass, concrete: GeneratedMappieDefinition): CodeGenerationModel {
        return when (this) {
            is ClassMappieCodeGenerationModel -> {
                val mappings = (mappings as TargetSourcesClassMappings)
                    .mapValues { (_, source) ->
                        val source = source.single()
                        if (source is TransformableClassMappingSource) {
                            val transformation = source.transformation
                            if (transformation is PropertyMappingViaMapperTransformation) {
                                if (transformation.mapper.clazz == original) {
                                    listOf(source.clone(transformation = transformation.copy(mapper = concrete)))
                                } else {
                                    listOf(source)
                                }
                            } else {
                                listOf(source)
                            }
                        } else {
                            listOf(source)
                        }
                    }

                ClassMappieCodeGenerationModel(
                    origin,
                    concrete,
                    constructor,
                    TargetSourcesClassMappings(mappings),
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