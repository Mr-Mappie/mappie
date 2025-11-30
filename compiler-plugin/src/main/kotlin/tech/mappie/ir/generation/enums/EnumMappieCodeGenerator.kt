package tech.mappie.ir.generation.enums

import org.jetbrains.kotlin.backend.common.lower.irThrow
import org.jetbrains.kotlin.ir.builders.*
import tech.mappie.ir.MappieContext
import tech.mappie.ir.generation.EnumMappieCodeGenerationModel
import tech.mappie.ir.generation.MappieCodeGenerator
import tech.mappie.ir.resolving.enums.ExplicitEnumMappingTarget
import tech.mappie.ir.resolving.enums.ResolvedEnumMappingTarget
import tech.mappie.ir.resolving.enums.ThrowingEnumMappingTarget
import tech.mappie.ir.referenceFunctionValueOf
import tech.mappie.ir.resolving.SourcesTargetEnumMappings

class EnumMappieCodeGenerator(
    override val model: EnumMappieCodeGenerationModel
) : MappieCodeGenerator(model) {

    private val mappings: SourcesTargetEnumMappings

    init {
        require(model.mappings is SourcesTargetEnumMappings)
        mappings = model.mappings
    }

    context(context: MappieContext)
    override fun IrBlockBodyBuilder.content() {
        +irReturn(irWhen(model.target, buildList {
            mappings.forEach { (source, target) ->
                val lhs = irGet(model.definition.referenceMapFunction().parameters[1])
                val rhs = irCall(source.referenceFunctionValueOf()).apply {
                    arguments[0] = irString(source.name.asString())
                }
                val target = target.single()
                add(irBranch(
                    irEquals(lhs, rhs), when (target) {
                        is ExplicitEnumMappingTarget -> construct(target)
                        is ResolvedEnumMappingTarget -> construct(target)
                        is ThrowingEnumMappingTarget -> construct(target)
                    }
                ))
            }
            add(irElseBranch(irCall(context.pluginContext.irBuiltIns.noWhenBranchMatchedExceptionSymbol)))
        }))
    }

    private fun construct(target: ExplicitEnumMappingTarget) =
        target.target

    context(context: MappieContext)
    private fun IrBlockBodyBuilder.construct(target: ResolvedEnumMappingTarget) =
        irCall(target.target.referenceFunctionValueOf()).apply {
            arguments[0] = irString(target.target.name.asString())
        }

    private fun IrBlockBodyBuilder.construct(target: ThrowingEnumMappingTarget) =
        irThrow(target.exception)
}