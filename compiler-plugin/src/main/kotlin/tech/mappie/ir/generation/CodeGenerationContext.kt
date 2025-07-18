package tech.mappie.ir.generation

import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.types.IrType
import tech.mappie.MappieContext
import tech.mappie.ir.resolving.MappieDefinition

class CodeGenerationContext(
    context: MappieContext,
    val model: CodeGenerationModel,
    val definitions: List<MappieDefinition>,
    val generated: Map<Pair<IrType, IrType>, IrClass>,
) : MappieContext by context {

    fun copy(
        model: CodeGenerationModel = this.model,
        definitions: List<MappieDefinition> = this.definitions,
        generated: Map<Pair<IrType, IrType>, IrClass> = this.generated
    ) = CodeGenerationContext(this, model, definitions, generated)
}
