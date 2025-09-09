package tech.mappie.ir.generation.enums

import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.backend.common.lower.irThrow
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrCall
import tech.mappie.ir.generation.CodeGenerationContext
import tech.mappie.ir.generation.EnumMappieCodeGenerationModel
import tech.mappie.ir.generation.referenceFunctionValueOf
import tech.mappie.ir.resolving.enums.ExplicitEnumMappingTarget
import tech.mappie.ir.resolving.enums.ResolvedEnumMappingTarget
import tech.mappie.ir.resolving.enums.ThrowingEnumMappingTarget
import tech.mappie.ir.util.blockBody
import tech.mappie.ir.util.irLambda
import tech.mappie.referenceFunctionRun

class EnumMappieCodeGenerator(private val context: CodeGenerationContext, private val model: EnumMappieCodeGenerationModel) {

    fun lambda(scope: Scope): IrCall =
        with(context.pluginContext.irBuiltIns.createIrBuilder(scope.scopeOwnerSymbol)) {
            irCall(this@EnumMappieCodeGenerator.context.referenceFunctionRun()).apply {
                arguments[0] = irLambda(model.declaration.returnType, model.declaration.returnType) {
                    content()
                }
            }
        }

    fun body(scope: Scope): IrBlockBody =
        context.pluginContext.blockBody(scope) {
            content()
        }

    private fun IrBlockBodyBuilder.content() {
        +irReturn(irWhen(model.target, buildList {
            model.mappings.forEach { (source, target) ->
                val lhs = irGet(model.declaration.parameters[1])
                val rhs = irCall(source.referenceFunctionValueOf()).apply {
                    arguments[0] = irString(source.name.asString())
                }
                add(
                    irBranch(
                        irEquals(lhs, rhs), when (target) {
                            is ExplicitEnumMappingTarget -> construct(target)
                            is ResolvedEnumMappingTarget -> construct(target)
                            is ThrowingEnumMappingTarget -> construct(target)
                        }
                    )
                )
            }
            add(irElseBranch(irCall(context.irBuiltIns.noWhenBranchMatchedExceptionSymbol)))
        }))
    }

    private fun construct(target: ExplicitEnumMappingTarget) =
        target.target

    private fun IrBlockBodyBuilder.construct(target: ResolvedEnumMappingTarget) =
        irCall(target.target.referenceFunctionValueOf()).apply {
            arguments[0] = irString(target.target.name.asString())
        }

    private fun IrBlockBodyBuilder.construct(target: ThrowingEnumMappingTarget) =
        irThrow(target.exception)
}