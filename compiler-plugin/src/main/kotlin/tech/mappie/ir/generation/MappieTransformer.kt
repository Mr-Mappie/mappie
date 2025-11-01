package tech.mappie.ir.generation

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.Scope
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.visitors.IrTransformer
import tech.mappie.ir.MappieContext
import tech.mappie.ir.generation.classes.ObjectMappieCodeGenerator
import tech.mappie.ir.generation.enums.EnumMappieCodeGenerator
import tech.mappie.ir.util.isMappieMapFunction
import tech.mappie.util.IDENTIFIER_MAPPING

class MappieTransformer(private val context: MappieContext, private val model: CodeGenerationModel)
    : IrElementTransformerVoidWithContext() {

    override fun visitClassNew(declaration: IrClass): IrStatement = declaration.apply {
        declaration.declarations.filterIsInstance<IrSimpleFunction>().first { it.isMappieMapFunction() }.apply {
            transform(MappieTransformer(context, model), null)
            isFakeOverride = false
        }
    }

    override fun visitFunctionNew(declaration: IrFunction) = declaration.apply {
        body = with(createScope(declaration)) {
            if (body == null) {
                context(context) {
                    when (model) {
                        is EnumMappieCodeGenerationModel -> EnumMappieCodeGenerator(model).body(scope)
                        is ClassMappieCodeGenerationModel -> ObjectMappieCodeGenerator(model).body(scope)
                    }
                }
            } else {
                declaration.body!!.transform(FunctionBodyTransformer(scope, model), context)
            }
        }
    }

    private class FunctionBodyTransformer(private val scope: Scope, private val model: CodeGenerationModel) : IrTransformer<MappieContext>() {

        override fun visitCall(expression: IrCall, data: MappieContext): IrExpression {
            return when (expression.symbol.owner.name) {
                IDENTIFIER_MAPPING -> {
                    context(data) {
                        when (model) {
                            is EnumMappieCodeGenerationModel -> EnumMappieCodeGenerator(model).lambda(scope)
                            is ClassMappieCodeGenerationModel -> ObjectMappieCodeGenerator(model).lambda(scope)
                        }
                    }
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
}