package tech.mappie.ir.generation.classes

import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.makeNullable
import org.jetbrains.kotlin.ir.types.typeOrFail
import org.jetbrains.kotlin.ir.types.typeOrNull
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import tech.mappie.ir.MappieContext
import tech.mappie.ir.analysis.Problem.Companion.error
import tech.mappie.ir.generation.ClassMappieCodeGenerationModel
import tech.mappie.ir.generation.CodeGenerationModel
import tech.mappie.ir.generation.CodeModelGenerationStage
import tech.mappie.ir.generation.IrMappieGeneratedClass
import tech.mappie.ir.resolving.ClassMappingRequest
import tech.mappie.ir.GeneratedMappieDefinition
import tech.mappie.ir.InternalMappieDefinition
import tech.mappie.ir.MappieDefinition
import tech.mappie.ir.resolving.TargetSourcesClassMappings
import tech.mappie.ir.resolving.ResolvingStage
import tech.mappie.ir.resolving.classes.sources.ClassMappingSource
import tech.mappie.ir.resolving.classes.sources.ExplicitClassMappingSource
import tech.mappie.ir.resolving.classes.sources.GeneratedViaMapperTransformation
import tech.mappie.ir.resolving.classes.sources.PropertyMappingViaMapperTransformation
import tech.mappie.ir.resolving.classes.sources.TransformableClassMappingSource
import tech.mappie.ir.resolving.classes.targets.ClassMappingTarget
import tech.mappie.ir.resolving.classes.targets.FunctionCallTarget
import tech.mappie.ir.resolving.classes.targets.SetterTarget
import tech.mappie.ir.resolving.classes.targets.ValueParameterTarget
import tech.mappie.ir.selection.SelectionStage
import tech.mappie.ir.util.arguments
import tech.mappie.ir.util.location

class ClassMappieCodeGenerationModelFactory {

    @Suppress("UNCHECKED_CAST")
    context (context: MappieContext)
    fun construct(request: ClassMappingRequest, definition: MappieDefinition): ClassMappieCodeGenerationModel {
        val mappings = (request.mappings as TargetSourcesClassMappings)
            .mapValues { (target, sources) -> select(target, sources) }
            .filter { it.value != null } as Map<ClassMappingTarget, ClassMappingSource>

        return ClassMappieCodeGenerationModel(
            definition.origin,
            definition,
            request.constructor,
            TargetSourcesClassMappings(mappings.mapValues { listOf(it.value) }),
            generated(request.origin, mappings)
        )
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
    private fun generated(origin: InternalMappieDefinition, mappings: Map<ClassMappingTarget, ClassMappingSource>): Map<GeneratedMappieDefinition, CodeGenerationModel> =
        mappings.entries.fold(mutableMapOf<GeneratedMappieDefinition, CodeGenerationModel>()) { acc, (target, source) ->
            acc.apply {
                fun isUnique(source: IrType, target: IrType) = acc.entries.none { it.key.target.makeNullable() == target.makeNullable() && it.key.source.makeNullable() == source.makeNullable() }

                val source = source as? TransformableClassMappingSource
                when (val transformation = source?.transformation) {
                    is PropertyMappingViaMapperTransformation -> {
                        if (transformation.target.arguments.isNotEmpty() && source.source.type.arguments.isNotEmpty()) {
                            val source = source.source.arguments.first().typeOrNull ?: context.pluginContext.irBuiltIns.anyType.makeNullable()
                            val target = transformation.target.arguments.first().typeOrFail

                            if (isUnique(source, target) && context.definitions.matching(origin, source, target).firstOrNull() == null) {
                                generate(origin, source, target)?.also {
                                    acc[it.first] = it.second
                                }
                            }
                        }
                    }
                    is GeneratedViaMapperTransformation -> {
                        val (source, target) = transformation.source.type to target.type
                        if (isUnique(source, target)) {
                            generate(origin, source, target)?.also {
                                acc[it.first] = it.second
                            }
                        }
                    }
                    else -> { }
                }
            }
        }.toMap()

    context(context: MappieContext)
    private fun generate(origin: InternalMappieDefinition, source: IrType, target: IrType): Pair<GeneratedMappieDefinition, CodeGenerationModel>? {
        val definition = GeneratedMappieDefinition(
            origin,
            IrMappieGeneratedClass.named(source, target),
            source,
            target,
        )

        context.definitions.generated.add(definition)

        val resolved = ResolvingStage.execute(definition)
        val selected = SelectionStage.execute(resolved.requests)

        selected.mappings
            .filter { !it.value.validation.isValid || (it.value.request as? ClassMappingRequest)?.mappings?.isEmpty() ?: false }
            .forEach { (_, request) ->
                context.logger.log(
                    error(
                        "No implicit mapping can be generated from ${source.dumpKotlinLike()} to ${target.dumpKotlinLike()}",
                        location(origin.clazz),
                        request.validation.problems.map { it.description }
                    )
                )
            }

        val requests = selected.mappings
            .filter { it.value.request != null && it.value.validation.isValid }
            .mapValues { it.value.request!! }
            .toMap()

        return if (requests.isNotEmpty()) {
            CodeModelGenerationStage.execute(requests).models.mapKeys { it.key as GeneratedMappieDefinition }.toList()
                .single()
        } else {
            null
        }
    }
}