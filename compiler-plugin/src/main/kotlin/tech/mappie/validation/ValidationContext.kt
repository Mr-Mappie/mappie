package tech.mappie.validation

import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.types.IrType
import tech.mappie.MappieContext
import tech.mappie.resolving.MappieDefinition

class ValidationContext(
    context: MappieContext,
    val definitions: List<MappieDefinition>,
    val generated: List<Pair<IrType, IrType>>,
    val function: IrFunction,
) : MappieContext by context {

    fun copy(generated: List<Pair<IrType, IrType>> = this.generated) = ValidationContext(
        this,
        definitions,
        generated,
        function
    )
}