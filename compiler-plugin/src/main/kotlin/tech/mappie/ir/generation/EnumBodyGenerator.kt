package tech.mappie.ir.generation

import org.jetbrains.kotlin.backend.common.ScopeWithIr
import org.jetbrains.kotlin.ir.builders.IrBlockBodyBuilder
import org.jetbrains.kotlin.ir.builders.irBranch
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irElseBranch
import org.jetbrains.kotlin.ir.builders.irEquals
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.builders.irString
import org.jetbrains.kotlin.ir.builders.irWhen
import org.jetbrains.kotlin.ir.expressions.IrBody
import tech.mappie.ir.CodeGenerationContext
import tech.mappie.ir.IrEnumEntryTarget
import tech.mappie.ir.IrEnumTarget
import tech.mappie.ir.UserDefinedEnumCodeGenerationModel
import tech.mappie.ir.util.blockBody
import tech.mappie.ir_old.generation.referenceFunctionValueOf

class EnumBodyGenerator(val context: CodeGenerationContext) {

    context(scope: ScopeWithIr)
    fun construct(model: UserDefinedEnumCodeGenerationModel): IrBody {
        return context.pluginContext.blockBody(scope.scope) {
            +irReturn(irWhen(model.target, buildList {
                model.mappings.forEach { (target, source) ->
                    add(
                        irBranch(
                        irEquals(
                            irGet(model.parameter),
                            irCall(source.referenceFunctionValueOf()).apply {
                                arguments[0] = irString(source.name.asString())
                            },
                        ),
                        target(target)
                    ))
                }
                add(irElseBranch(irCall(context.irBuiltIns.noWhenBranchMatchedExceptionSymbol)))
            }))
        }
    }

    private fun IrBlockBodyBuilder.target(target: IrEnumTarget) = when (target) {
        is IrEnumEntryTarget -> irCall(target.entry.referenceFunctionValueOf()).apply {
            arguments[0] = irString(target.entry.name.asString())
        }
    }
}
