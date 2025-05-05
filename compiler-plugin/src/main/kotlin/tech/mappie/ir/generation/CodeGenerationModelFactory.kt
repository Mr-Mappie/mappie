package tech.mappie.ir.generation

import org.jetbrains.kotlin.ir.declarations.IrFunction
import tech.mappie.ir.generation.classes.mapping.ClassMappieCodeGenerationModelFactory
import tech.mappie.ir.generation.classes.updating.ClassUpdateCodeGenerationModelFactory
import tech.mappie.ir.generation.enums.EnumMappieCodeGenerationModelFactory
import tech.mappie.ir.resolving.ClassMappingRequest
import tech.mappie.ir.resolving.ClassUpdateRequest
import tech.mappie.ir.resolving.EnumMappingRequest
import tech.mappie.ir.resolving.MappingRequest

fun interface CodeGenerationModelFactory {

    fun construct(function: IrFunction): CodeGenerationModel

    companion object {
        fun of(request: MappingRequest): CodeGenerationModelFactory =
            when (request) {
                is ClassMappingRequest -> ClassMappieCodeGenerationModelFactory(request)
                is ClassUpdateRequest -> ClassUpdateCodeGenerationModelFactory(request)
                is EnumMappingRequest -> EnumMappieCodeGenerationModelFactory(request)
            }
    }
}