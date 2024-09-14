package tech.mappie.generation

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.*
import tech.mappie.generation.classes.ObjectMappieCodeGenerator
import tech.mappie.generation.enums.EnumMappieCodeGenerator

class MappieCodeGenerator(private val context: CodeGenerationContext) : IrElementTransformerVoidWithContext() {

    override fun visitClassNew(declaration: IrClass): IrStatement =
        declaration.declarations.filterIsInstance<IrSimpleFunction>().first { it == context.model.declaration }.apply {
            transform(this@MappieCodeGenerator, null)
            isFakeOverride = false
        }

    override fun visitFunctionNew(declaration: IrFunction) =
        declaration.apply {
            body = with(createScope(declaration)) {
                when (context.model) {
                    is EnumMappieCodeGenerationModel -> EnumMappieCodeGenerator(context, context.model).construct(scope)
                    is ClassMappieCodeGenerationModel -> ObjectMappieCodeGenerator(context, context.model).construct(scope)
                }
            }
        }
}
