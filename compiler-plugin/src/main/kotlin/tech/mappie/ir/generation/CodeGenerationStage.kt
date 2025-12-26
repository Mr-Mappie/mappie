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
            var model = model
            if (model is ClassMappieCodeGenerationModel) {
                model.generated.forEach {
                    val generated = GeneratedMappieClassConstructor()
                        .construct(definition.clazz, it.key, it.value)

                    val model = it.value.replaceTranformationStubs(it.key.clazz as IrMappieGeneratedClass, generated)
                    execute(mapOf(generated to model))
                }

                model = model.replaceTranformationStubs()
            }

            definition.clazz.transform(MappieTransformer(context, model), null)
        }

        return CodeGenerationResult(elements.filterIsInstance<IrClass>())
    }

    context(context: MappieContext)
    private fun ClassMappieCodeGenerationModel.replaceTranformationStubs(): ClassMappieCodeGenerationModel {
        val mappings: ClassMappings = TargetSourcesClassMappings(when (mappings) {
            is TargetSourcesClassMappings -> {
                mappings.map {  (target, sources) ->
                    val source = sources.single()
                    target to listOf(
                        when (source) {
                            is TransformableClassMappingSource -> {
                                val transformation = source.transformation
                                if (transformation is PropertyMappingViaMapperTransformation && transformation.mapper is GeneratedMappieDefinition && transformation.mapper.clazz is IrMappieGeneratedClass) {
                                    val concrete = context.definitions.named(
                                        (source.transformation as PropertyMappingViaMapperTransformation).mapper.clazz.name,
                                        definition.clazz
                                    )
                                    source.clone(transformation = transformation.copy(mapper = concrete))
                                } else {
                                    source
                                }
                            }
                            else -> {
                                source
                            }
                        }
                    )
                }.toMap()
            }
        })
        return copy(mappings = mappings)
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