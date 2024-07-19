package tech.mappie.generation

import tech.mappie.resolving.*
import tech.mappie.util.*
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.util.*

class MappieIrGenerator(private val generation: MappieGeneration) : IrElementTransformerVoidWithContext() {

    override fun visitClassNew(declaration: IrClass): IrStatement {
        declaration.declarations.filterIsInstance<IrClass>().forEach { inner ->
            inner.transform(MappieIrGenerator(generation), null)
        }

        if (declaration.accept(ShouldTransformCollector(declaration.fileEntry), Unit)) {
            val function = declaration.declarations
                .filterIsInstance<IrSimpleFunction>()
                .first { it.isMappieMapFunction() }

            if (function.isFakeOverride) {
                function.isFakeOverride = false
            }

            val transformed = function.transform(this, null)
            if (transformed is IrSimpleFunction && transformed.body == null) {
                declaration.declarations.remove(transformed)
            }
        }
        return declaration
    }

    override fun visitFunctionNew(declaration: IrFunction): IrStatement {
        val mapping = generation.mappings[declaration]
        if (mapping != null) {
            declaration.body = with(createScope(declaration)) {
                when (mapping) {
                    is ConstructorCallMapping -> ConstructorCallMappingConstructor(mapping, declaration).construct(scope)
                    is EnumMapping -> EnumMappingConstructor(mapping, declaration).construct(scope)
                }
            }
        }
        return declaration
    }
}
