package tech.mappie.generation

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.types.IrType
import tech.mappie.MappieContext
import tech.mappie.resolving.MappieDefinition

class CodeGenerationContext(
    context: MappieContext,
    val model: CodeGenerationModel,
    val definitions: List<MappieDefinition>,
    val generated: Map<Pair<IrType, IrType>, IrClass>
) : MappieContext by context {

    fun with(key: Pair<IrType, IrType>, clazz: IrClass): CodeGenerationContext =
        CodeGenerationContext(
            this,
            model,
            definitions,
            buildMap {
                putAll(generated)
                put(key, clazz)
            },
        )
}