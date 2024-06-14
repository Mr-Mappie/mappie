package io.github.mappie

import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.builders.IrBlockBodyBuilder
import org.jetbrains.kotlin.ir.builders.IrGeneratorContextBase
import org.jetbrains.kotlin.ir.builders.Scope

class MappiePluginContext(
    val messageCollector: MessageCollector,
    val configuration: MappieConfiguration,
    irPluginContext: IrPluginContext,
): IrPluginContext by irPluginContext {

    fun blockBody(scope: Scope, body: IrBlockBodyBuilder.() -> Unit) =
        IrBlockBodyBuilder(IrGeneratorContextBase(irBuiltIns), scope, scope.scopeOwnerSymbol.owner.startOffset, scope.scopeOwnerSymbol.owner.endOffset)
            .blockBody(body)

}
