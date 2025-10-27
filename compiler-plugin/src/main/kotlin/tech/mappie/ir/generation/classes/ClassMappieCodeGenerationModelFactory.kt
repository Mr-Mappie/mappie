package tech.mappie.ir.generation.classes

import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.name.Name.identifier
import tech.mappie.ir.MappieContext
import tech.mappie.ir.generation.ClassMappieCodeGenerationModel
import tech.mappie.ir.generation.CodeGenerationModel
import tech.mappie.ir.generation.CodeModelGenerationStage
import tech.mappie.ir.generation.IrLazyGeneratedClass
import tech.mappie.ir.resolving.ClassMappingRequest
import tech.mappie.ir.resolving.GeneratedMappieDefinition
import tech.mappie.ir.resolving.InternalMappieDefinition
import tech.mappie.ir.resolving.MappieDefinition
import tech.mappie.ir.resolving.ResolvingStage
import tech.mappie.ir.resolving.classes.sources.ClassMappingSource
import tech.mappie.ir.resolving.classes.sources.ExplicitClassMappingSource
import tech.mappie.ir.resolving.classes.sources.GeneratedViaMapperTransformation
import tech.mappie.ir.resolving.classes.sources.TransformableClassMappingSource
import tech.mappie.ir.resolving.classes.targets.ClassMappingTarget
import tech.mappie.ir.resolving.classes.targets.FunctionCallTarget
import tech.mappie.ir.resolving.classes.targets.SetterTarget
import tech.mappie.ir.resolving.classes.targets.ValueParameterTarget
import tech.mappie.ir.selection.SelectionStage

class ClassMappieCodeGenerationModelFactory(private val request: ClassMappingRequest) {

    @Suppress("UNCHECKED_CAST")
    context (context: MappieContext)
    fun construct(definition: MappieDefinition): ClassMappieCodeGenerationModel {
        val mappings = request.mappings
            .mapValues { (target, sources) -> select(target, sources) }
            .filter { it.value != null } as Map<ClassMappingTarget, ClassMappingSource>

        return ClassMappieCodeGenerationModel(definition, request.constructor, mappings, generated(request.origin, mappings))
    }

    private fun select(target: ClassMappingTarget, sources: List<ClassMappingSource>): ClassMappingSource? =
        if (sources.isEmpty()) {
            null
        } else {
            sources.singleOrNull() ?: when (target) {
                is FunctionCallTarget -> sources.first()
                is SetterTarget -> sources.first()
                is ValueParameterTarget -> sources.first { it is ExplicitClassMappingSource }
            }
        }

    context (context: MappieContext)
    private fun generated(origin: InternalMappieDefinition, mappings: Map<ClassMappingTarget, ClassMappingSource>): Map<GeneratedMappieDefinition, CodeGenerationModel> {
        return mappings.mapNotNull { (target, source) ->
            if (source is TransformableClassMappingSource) {
                val transformation = source.transformation
                if (transformation is GeneratedViaMapperTransformation) {
                    val definition = GeneratedMappieDefinition(
                        IrLazyGeneratedClass(identifier(transformation.source.type.dumpKotlinLike() + "To" + target.type.dumpKotlinLike() + "Mapper")),
                        transformation.source.type,
                        target.type,
                    )

                    context.definitions.generated.add(definition)

                    val resolved = ResolvingStage.execute(origin, definition)
                    val selected = SelectionStage.execute(resolved.requests)
                    CodeModelGenerationStage.execute(selected.mappings).models.mapKeys { it.key as GeneratedMappieDefinition }.toList().single()
                } else {
                    null
                }
            } else {
                null
            }
        }.toMap()
    }
}