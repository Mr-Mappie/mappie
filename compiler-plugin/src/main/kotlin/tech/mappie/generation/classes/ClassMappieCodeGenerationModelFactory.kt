package tech.mappie.generation.classes

import org.jetbrains.kotlin.ir.declarations.IrFunction
import tech.mappie.generation.ClassMappieCodeGenerationModel
import tech.mappie.generation.CodeGenerationModelFactory
import tech.mappie.resolving.ClassMappingRequest
import tech.mappie.resolving.classes.sources.ClassMappingSource
import tech.mappie.resolving.classes.sources.ExplicitClassMappingSource
import tech.mappie.resolving.classes.targets.ClassMappingTarget
import tech.mappie.resolving.classes.targets.FunctionCallTarget
import tech.mappie.resolving.classes.targets.SetterTarget
import tech.mappie.resolving.classes.targets.ValueParameterTarget

class ClassMappieCodeGenerationModelFactory(private val request: ClassMappingRequest) : CodeGenerationModelFactory {

    @Suppress("UNCHECKED_CAST")
    override fun construct(function: IrFunction): ClassMappieCodeGenerationModel =
        ClassMappieCodeGenerationModel(
            function,
            request.constructor,
            request.mappings
                .mapValues { (target, sources) -> select(target, sources) }
                .filter { it.value != null } as Map<ClassMappingTarget, ClassMappingSource>
        )

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
}