package tech.mappie.ir.generation.enums

import org.jetbrains.kotlin.backend.common.lower.createIrBuilder
import org.jetbrains.kotlin.backend.common.lower.irThrow
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.expressions.IrBlockBody
import org.jetbrains.kotlin.ir.expressions.IrCall
import tech.mappie.ir.MappieContext
import tech.mappie.ir.generation.EnumMappieCodeGenerationModel
import tech.mappie.ir.generation.referenceFunctionValueOf
import tech.mappie.ir.resolving.enums.ExplicitEnumMappingTarget
import tech.mappie.ir.resolving.enums.ResolvedEnumMappingTarget
import tech.mappie.ir.resolving.enums.ThrowingEnumMappingTarget
import tech.mappie.ir.util.blockBody
import tech.mappie.ir.util.irLambda
import tech.mappie.ir.referenceFunctionRun

class EnumMappieCodeGenerator(private val model: EnumMappieCodeGenerationModel) {

    context(context: MappieContext)
    fun lambda(scope: Scope): IrCall =
        with(context.pluginContext.irBuiltIns.createIrBuilder(scope.scopeOwnerSymbol)) {
            irCall(referenceFunctionRun()).apply {
                arguments[0] = irLambda(model.definition.referenceMapFunction().returnType, model.definition.referenceMapFunction().returnType) {
                    content()
                }
            }
        }

    context(context: MappieContext)
    fun body(scope: Scope): IrBlockBody =
        context.pluginContext.blockBody(scope) {
            content()
        }

    private fun IrBlockBodyBuilder.content() {
        +irReturn(irWhen(model.target, buildList {
            model.mappings.forEach { (source, target) ->
                val lhs = irGet(model.definition.referenceMapFunction().parameters[1])
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