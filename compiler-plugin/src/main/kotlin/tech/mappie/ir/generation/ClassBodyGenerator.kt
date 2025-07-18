package tech.mappie.ir.generation

import org.jetbrains.kotlin.backend.common.ScopeWithIr
import org.jetbrains.kotlin.ir.builders.IrBuilderWithScope
import org.jetbrains.kotlin.ir.builders.createTmpVariable
import org.jetbrains.kotlin.ir.builders.irCall
import org.jetbrains.kotlin.ir.builders.irCallConstructor
import org.jetbrains.kotlin.ir.builders.irGet
import org.jetbrains.kotlin.ir.builders.irReturn
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.expressions.IrExpression
import tech.mappie.ir.CodeGenerationContext
import tech.mappie.ir.IrExplicitPropertyClassSource
import tech.mappie.ir.IrImplicitPropertyClassSource
import tech.mappie.ir.IrClassSource
import tech.mappie.ir.UserDefinedClassCodeGenerationModel
import tech.mappie.ir.util.blockBody
import kotlin.collections.component1
import kotlin.collections.component2

class ClassBodyGenerator(val context: CodeGenerationContext) {

    context(scope: ScopeWithIr)
    fun construct(model: UserDefinedClassCodeGenerationModel): IrBody {
        return context.pluginContext.blockBody(scope.scope) {
            val call = irCallConstructor(model.constructor.symbol, emptyList()).apply {
                model.mappings.forEach { (target, source) ->
                    constructArgument(source).let { argument ->
                        arguments[target.parameter.indexInParameters] = argument
                    }
                }
            }

            val variable = createTmpVariable(call)

            +irReturn(irGet(variable))
        }
    }

    fun IrBuilderWithScope.constructArgument(source: IrClassSource): IrExpression {
        when (source) {
            is IrImplicitPropertyClassSource -> {
                val getter = irCall(source.property.getter!!).apply {
                    dispatchReceiver = irGet(source.receiver)
                }
                return getter
            }
            is IrExplicitPropertyClassSource -> {
                TODO()
//                val getter = irCall(source.property.getter!!).apply {
//                    dispatchReceiver = source.receiver
//                }
//                return getter
            }
        }
    }
}
