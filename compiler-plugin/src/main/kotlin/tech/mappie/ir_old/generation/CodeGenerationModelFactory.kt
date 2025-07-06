package tech.mappie.ir_old.generation

import org.jetbrains.kotlin.ir.declarations.IrFunction
import tech.mappie.ir_old.generation.classes.ClassMappieCodeGenerationModelFactory
import tech.mappie.ir_old.generation.enums.EnumMappieCodeGenerationModelFactory
import tech.mappie.ir_old.resolving.ClassMappingRequest
import tech.mappie.ir_old.resolving.EnumMappingRequest
import tech.mappie.ir_old.resolving.MappingRequest

fun interface CodeGenerationModelFactory {

    fun construct(function: IrFunction): CodeGenerationModel

    companion object {
        fun of(request: MappingRequest): CodeGenerationModelFactory =
            when (request) {
                is ClassMappingRequest -> ClassMappieCodeGenerationModelFactory(request)
                is EnumMappingRequest -> EnumMappieCodeGenerationModelFactory(request)
            }
    }
}