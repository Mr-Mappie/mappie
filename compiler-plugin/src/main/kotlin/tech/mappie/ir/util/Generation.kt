package tech.mappie.ir.util

import org.jetbrains.kotlin.ir.builders.IrBlockBodyBuilder
import org.jetbrains.kotlin.ir.builders.IrGeneratorContextBase
import org.jetbrains.kotlin.ir.builders.IrGeneratorContextInterface
import org.jetbrains.kotlin.ir.builders.Scope

fun IrGeneratorContextInterface.blockBody(scope: Scope, body: IrBlockBodyBuilder.() -> Unit) =
    IrBlockBodyBuilder(
        IrGeneratorContextBase(irBuiltIns),
        scope,
        scope.scopeOwnerSymbol.owner.startOffset,
        scope.scopeOwnerSymbol.owner.endOffset,
    ).blockBody(body)
