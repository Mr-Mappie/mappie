package tech.mappie.ir.generation.classes.updating

import org.jetbrains.kotlin.ir.declarations.IrFunction
import tech.mappie.exceptions.MappiePanicException.Companion.panic
import tech.mappie.ir.generation.ClassUpdateCodeGenerationModel
import tech.mappie.ir.generation.CodeGenerationModelFactory
import tech.mappie.ir.resolving.ClassUpdateRequest
import tech.mappie.ir.resolving.classes.sources.ClassMappingSource
import tech.mappie.ir.resolving.classes.targets.ClassMappingTarget
import tech.mappie.ir.resolving.classes.targets.FunctionCallTarget
import tech.mappie.ir.resolving.classes.targets.SetterTarget
import tech.mappie.ir.resolving.classes.targets.ValueParameterTarget

class ClassUpdateCodeGenerationModelFactory(private val request: ClassUpdateRequest) : CodeGenerationModelFactory {

    override fun construct(function: IrFunction): ClassUpdateCodeGenerationModel =
        ClassUpdateCodeGenerationModel(
            function,
            request.name,
            request.mappings.mapValues { (target, sources) ->
                if (sources.isEmpty()) {
                    null
                } else {
                    sources.singleOrNull() ?: when (target) {
                        is FunctionCallTarget -> sources.first()
                        is SetterTarget -> sources.first()
                        is ValueParameterTarget -> panic("ValueParameterTarget should not occur in ClassUpdateCodeGenerationModelFactory")
                    }
                }
            }  as Map<ClassMappingTarget, ClassMappingSource>
        )
}