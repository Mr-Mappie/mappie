package tech.mappie.ir.resolving

import org.jetbrains.kotlin.ir.declarations.IrFunction
import tech.mappie.MappieContext

class ResolverContext(
    context: MappieContext,
    val definitions: List<MappieDefinition>,
    val origin: IrFunction,
) : MappieContext by context