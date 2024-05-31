package io.github.stefankoppier.mapping

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.builders.IrBlockBodyBuilder
import org.jetbrains.kotlin.ir.builders.IrGeneratorContextBase
import org.jetbrains.kotlin.ir.builders.Scope
import org.jetbrains.kotlin.ir.util.SYNTHETIC_OFFSET

class MappingPluginContext(
    val messageCollector: MessageCollector,
    irPluginContext: IrPluginContext,
): IrPluginContext by irPluginContext {

    fun blockBody(scope: Scope, body: IrBlockBodyBuilder.() -> Unit) =
        IrBlockBodyBuilder(IrGeneratorContextBase(irBuiltIns), scope, SYNTHETIC_OFFSET, SYNTHETIC_OFFSET)
            .blockBody(body)

}
