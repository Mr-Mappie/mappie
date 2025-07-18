package tech.mappie.ir.generation

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.util.functions
import tech.mappie.ir.CodeGenerationContext
import tech.mappie.ir.CodeGenerationModel
import tech.mappie.ir.UserDefinedClassCodeGenerationModel
import tech.mappie.ir.UserDefinedEnumCodeGenerationModel
import tech.mappie.ir_old.util.isMappieMapFunction

class ClassTransformer(val context: CodeGenerationContext, val model: CodeGenerationModel) : IrElementTransformerVoidWithContext() {

    override fun visitClassNew(declaration: IrClass): IrStatement {
        return declaration.apply {
            functions.single { it.isMappieMapFunction() }.apply {
                transform()
                isFakeOverride = false
            }
        }
    }

    override fun visitFunctionNew(declaration: IrFunction): IrStatement {
        return declaration.apply {
            context(createScope(declaration)) {
                body = when (model) {
                    is UserDefinedClassCodeGenerationModel -> ClassBodyGenerator(context).construct(model)
                    is UserDefinedEnumCodeGenerationModel -> EnumBodyGenerator(context).construct(model)
                }
            }
        }
    }

    private fun IrElement.transform() =
        transform(this@ClassTransformer, null)
}