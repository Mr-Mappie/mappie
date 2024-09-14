package tech.mappie.generation.enums

import org.jetbrains.kotlin.backend.common.lower.irThrow
import org.jetbrains.kotlin.ir.builders.*
import tech.mappie.generation.CodeGenerationContext
import tech.mappie.generation.EnumMappieCodeGenerationModel
import tech.mappie.generation.referenceFunctionValueOf
import tech.mappie.resolving.enums.ExplicitEnumMappingTarget
import tech.mappie.resolving.enums.ResolvedEnumMappingTarget
import tech.mappie.resolving.enums.ThrowingEnumMappingTarget
import tech.mappie.util.blockBody

class EnumMappieCodeGenerator(private val context: CodeGenerationContext, private val model: EnumMappieCodeGenerationModel) {

    fun construct(scope: Scope) =
        context.pluginContext.blockBody(scope) {
            +irReturn(irWhen(model.target, buildList {
                model.mappings.forEach { (source, target) ->
                    val lhs = irGet(model.declaration.valueParameters.first())
                    val rhs = irCall(source.referenceFunctionValueOf()).apply {
                        putValueArgument(0, irString(source.name.asString()))
                    }
                    add(irBranch(irEquals(lhs, rhs), when (target) {
                        is ExplicitEnumMappingTarget -> construct(target)
                        is ResolvedEnumMappingTarget -> construct(target)
                        is ThrowingEnumMappingTarget -> construct(target)
                    }))
                }
                add(irElseBranch(irCall(context.irBuiltIns.noWhenBranchMatchedExceptionSymbol)))
            }))
        }


    private fun IrBlockBodyBuilder.construct(target: ExplicitEnumMappingTarget) =
        target.target

    private fun IrBlockBodyBuilder.construct(target: ResolvedEnumMappingTarget) =
        irCall(target.target.referenceFunctionValueOf()).apply {
            putValueArgument(0, irString(target.target.name.asString()))
        }

    private fun IrBlockBodyBuilder.construct(target: ThrowingEnumMappingTarget) =
        irThrow(target.exception)
}