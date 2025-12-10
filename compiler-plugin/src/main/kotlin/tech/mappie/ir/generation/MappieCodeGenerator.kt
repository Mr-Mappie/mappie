package tech.mappie.ir.generation

import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.ir.builders.IrBlockBodyBuilder
import org.jetbrains.kotlin.ir.builders.Scope
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.util.getKFunctionType
import tech.mappie.ir.MappieContext
import tech.mappie.ir.referenceFunctionRun
import tech.mappie.ir.util.blockBody
import tech.mappie.ir.util.irLambda

abstract class MappieCodeGenerator(protected open val model: CodeGenerationModel) {

    context(context: MappieContext)
    fun lambda(scope: Scope): IrCall =
        with(context.pluginContext.irBuiltIns.createIrBuilder(scope.scopeOwnerSymbol)) {
            val target = model.definition.referenceMapFunction().returnType
            irCall(referenceFunctionRun()).apply {
                arguments[0] = irLambda(
                    target,
                    context.pluginContext.irBuiltIns.getKFunctionType(target, emptyList())
                ) {
                    content()
                }
            }
        }

    context(context: MappieContext)
    fun body(scope: Scope): IrBlockBody =
        context.pluginContext.blockBody(scope) {
            content()
        }

    context(context: MappieContext)
    protected abstract fun IrBlockBodyBuilder.content()
}