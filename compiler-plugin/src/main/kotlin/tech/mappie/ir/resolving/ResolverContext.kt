package tech.mappie.ir.resolving

import org.jetbrains.kotlin.ir.declarations.IrFunction
import tech.mappie.MappieContext

class ResolverContext(
    context: MappieContext,
    val definitions: List<MappieDefinition>,
    val function: IrFunction? = null,
) : MappieContext by context {

    constructor(context: ResolverContext, function: IrFunction) : this(context, context.definitions, function)
}