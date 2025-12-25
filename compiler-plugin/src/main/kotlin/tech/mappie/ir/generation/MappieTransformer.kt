package tech.mappie.ir.generation

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.Scope
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.visitors.IrTransformer
import tech.mappie.exceptions.MappiePanicException.Companion.panic
import tech.mappie.ir.MappieContext
import tech.mappie.ir.generation.classes.ClassMappieCodeGenerator
import tech.mappie.ir.generation.enums.EnumMappieCodeGenerator
import tech.mappie.ir.resolving.SourcesTargetEnumMappings
import tech.mappie.ir.resolving.SuperCallEnumMappings
import tech.mappie.ir.util.isMappieMapFunction
import tech.mappie.util.IDENTIFIER_MAPPING

class MappieTransformer(private val context: MappieContext, private val model: CodeGenerationModel)
    : IrElementTransformerVoidWithContext() {

    override fun visitClassNew(declaration: IrClass): IrStatement = declaration.apply {
        declaration.declarations.filterIsInstance<IrSimpleFunction>().first { it.isMappieMapFunction() }.apply {
            if (model.mappings !is SuperCallEnumMappings) {
                transform(MappieTransformer(context, model), null)
                isFakeOverride = false
            }
        }
    }

    override fun visitFunctionNew(declaration: IrFunction) = declaration.apply {
        body = with(createScope(declaration)) {
            if (body == null) {
                context(context) { generator(model).body(scope) }
            } else {
                declaration.body!!.transform(FunctionBodyTransformer(scope, model), context)
            }
        }
    }

    private class FunctionBodyTransformer(private val scope: Scope, private val model: CodeGenerationModel) : IrTransformer<MappieContext>() {

        override fun visitCall(expression: IrCall, data: MappieContext): IrExpression {
            return when (expression.symbol.owner.name) {
                IDENTIFIER_MAPPING -> {
                    context(data) { generator(model).lambda(scope) }
                }
                else -> {
                    expression.arguments.forEachIndexed { index, argument ->
                        expression.arguments[index] = argument?.transform(this, data)
                    }
                    expression
                }
            }
        }
    }

    companion object {
        private fun generator(model: CodeGenerationModel) = when (model) {
            is ClassMappieCodeGenerationModel -> {
                ClassMappieCodeGenerator(model)
            }
            is EnumMappieCodeGenerationModel -> {
                when (model.mappings) {
                    is SourcesTargetEnumMappings -> EnumMappieCodeGenerator(model)
                    is SuperCallEnumMappings -> panic("Super call should be handled earlier")
                }
            }
        }
    }
}