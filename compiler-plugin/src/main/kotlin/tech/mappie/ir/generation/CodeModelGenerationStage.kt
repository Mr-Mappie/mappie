package tech.mappie.ir.generation

import tech.mappie.ir.MappieContext
import tech.mappie.ir.generation.classes.ClassMappieCodeGenerationModelFactory
import tech.mappie.ir.generation.enums.EnumMappieCodeGenerationModelFactory
import tech.mappie.ir.resolving.ClassMappingRequest
import tech.mappie.ir.resolving.EnumMappingRequest
import tech.mappie.ir.MappieDefinition
import tech.mappie.ir.resolving.MappingRequest
import kotlin.collections.component1
import kotlin.collections.component2

object CodeModelGenerationStage {

    context(context: MappieContext)
    fun execute(mappings: Map<MappieDefinition, MappingRequest>): CodeModelGenerationResult {
        val models = mappings.mapValues { (definition, request) ->
            val factory = ClassMappieCodeGenerationModelFactory()
            construct(definition, request, factory).also { factory.reportMissingMappings(request.origin) }
        }
        return CodeModelGenerationResult(models)
    }

    context(context: MappieContext)
    internal fun construct(
        definition: MappieDefinition,
        request: MappingRequest,
        factory: ClassMappieCodeGenerationModelFactory,
    ): CodeGenerationModel = when (request) {
        is ClassMappingRequest -> factory.construct(request, definition)
        is EnumMappingRequest -> EnumMappieCodeGenerationModelFactory().construct(request, definition)
    }
}

data class CodeModelGenerationResult(val models: Map<MappieDefinition, CodeGenerationModel>)
