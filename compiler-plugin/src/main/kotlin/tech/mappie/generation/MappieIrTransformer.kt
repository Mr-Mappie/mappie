package tech.mappie.generation

import tech.mappie.resolving.*
import tech.mappie.util.*
import tech.mappie.validation.MappingValidation
import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.util.*

class MappieIrTransformer(private val symbols: List<MappieDefinition>) : IrElementTransformerVoidWithContext() {

    override fun visitClassNew(declaration: IrClass): IrStatement {
        declaration.declarations.filterIsInstance<IrClass>().forEach { inner ->
            inner.transform(MappieIrTransformer(symbols), null)
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
        if (declaration.accept(ShouldTransformCollector(declaration.fileEntry), Unit)) {
            val file = declaration.fileEntry

            val (valids, invalids) = declaration
                .accept(MappingResolver(file), symbols)
                .map { it to MappingValidation.of(file, it) }
                .partition { it.second.isValid() }

            if (valids.isNotEmpty()) {
                declaration.body = with(createScope(declaration)) {
                    val (mapping, validation) = MappingSelector.of(valids).select()

                    logAll(validation.warnings(), location(declaration))

                    when (mapping) {
                        is ConstructorCallMapping -> ConstructorCallMappingConstructor(mapping, declaration).construct(scope)
                        is EnumMapping -> EnumMappingConstructor(mapping, declaration).construct(scope)
                    }
                }
            } else {
                val first = invalids.firstOrNull()
                if (first != null) {
                    logAll(invalids.first().second.problems, location(declaration))
                } else {
                    logError("No constructor visible to use", location(declaration))
                }
            }
        }
        return declaration
    }
}
