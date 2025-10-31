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
            when (request) {
                is ClassMappingRequest -> execute(definition, request)
                is EnumMappingRequest -> execute(definition, request)
            }
        }
        return CodeModelGenerationResult(models)
    }

    context(context: MappieContext)
    private fun execute(definition: MappieDefinition, request: ClassMappingRequest): ClassMappieCodeGenerationModel {
        return ClassMappieCodeGenerationModelFactory().construct(request, definition)
    }

    private fun execute(definition: MappieDefinition, request: EnumMappingRequest): EnumMappieCodeGenerationModel {
        return EnumMappieCodeGenerationModelFactory().construct(request, definition)
    }
}

data class CodeModelGenerationResult(val models: Map<MappieDefinition, CodeGenerationModel>)
