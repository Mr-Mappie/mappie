package tech.mappie.generation

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.*
import tech.mappie.generation.classes.ObjectMappieCodeGenerator
import tech.mappie.generation.enums.EnumMappieCodeGenerator
import tech.mappie.resolving.MappingResolver
import tech.mappie.resolving.ResolverContext
import tech.mappie.selection.MappingSelector
import tech.mappie.util.isMappieMapFunction
import tech.mappie.util.mappieType

class MappieCodeGenerator(private val context: CodeGenerationContext) : IrElementTransformerVoidWithContext() {

    override fun visitClassNew(declaration: IrClass): IrStatement = declaration.apply {
        val context = if (context.model is ClassMappieCodeGenerationModel) {
            val models = context.model.mappings.values
                .filter { source -> source.hasGeneratedTransformationMapping() }
                .map { source -> source.selectGeneratedTransformationMapping() }
                .distinctBy { it.source.type to it.target.type }
                .map { transformation ->
                    val options = MappingResolver.of(
                        transformation.source.type.mappieType(),
                        transformation.target.type.mappieType(),
                        ResolverContext(context, context.definitions, context.model.declaration)
                    ).resolve(null)
                    MappingSelector.of(options).select()!!.first!!
                }

            models.fold(context) { context, request ->
                GeneratedMappieClassConstructor(context).construct(request, declaration)?.let { (context, generated) ->
                    declarations.add(generated)
                    context
                } ?: context
            }
        } else {
            context
        }

        declaration.declarations.filterIsInstance<IrSimpleFunction>().first { it.isMappieMapFunction() }.apply {
            val model = when (context.model) {
                is ClassMappieCodeGenerationModel -> context.model.copy(declaration = this)
                is EnumMappieCodeGenerationModel -> context.model.copy(declaration = this)
            }
            transform(MappieCodeGenerator(context.copy(model = model)), null)
            isFakeOverride = false
        }
    }

    override fun visitFunctionNew(declaration: IrFunction) = declaration.apply {
        body = with(createScope(declaration)) {
            when (context.model) {
                is EnumMappieCodeGenerationModel -> EnumMappieCodeGenerator(context, context.model).construct(scope)
                is ClassMappieCodeGenerationModel -> ObjectMappieCodeGenerator(context, context.model).construct(scope)
            }
        }
    }
}
