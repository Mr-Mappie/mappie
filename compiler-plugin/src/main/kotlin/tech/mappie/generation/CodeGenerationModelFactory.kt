package tech.mappie.generation

import org.jetbrains.kotlin.ir.declarations.IrFunction
import tech.mappie.generation.classes.ClassMappieCodeGenerationModelFactory
import tech.mappie.generation.enums.EnumMappieCodeGenerationModelFactory
import tech.mappie.resolving.ClassMappingRequest
import tech.mappie.resolving.EnumMappingRequest
import tech.mappie.resolving.MappingRequest

interface CodeGenerationModelFactory {

    fun construct(function: IrFunction): CodeGenerationModel

    companion object {
        fun of(request: MappingRequest): CodeGenerationModelFactory =
            when (request) {
                is ClassMappingRequest -> ClassMappieCodeGenerationModelFactory(request)
                is EnumMappingRequest -> EnumMappieCodeGenerationModelFactory(request)
            }
    }
}