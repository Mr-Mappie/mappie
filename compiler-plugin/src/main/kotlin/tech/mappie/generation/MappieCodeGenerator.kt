package tech.mappie.generation

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.*
import tech.mappie.generation.classes.ObjectMappieCodeGenerator
import tech.mappie.generation.enums.EnumMappieCodeGenerator
import tech.mappie.resolving.MappingResolver
import tech.mappie.resolving.ResolverContext
import tech.mappie.resolving.classes.sources.GeneratedViaMapperTransformation
import tech.mappie.resolving.classes.sources.ImplicitPropertyMappingSource
import tech.mappie.util.isMappieMapFunction
import tech.mappie.util.mappieType

class MappieCodeGenerator(private val context: CodeGenerationContext) : IrElementTransformerVoidWithContext() {

    override fun visitClassNew(declaration: IrClass): IrStatement = declaration.apply {
        // TODO: Use MappingSelector instead?
        val context = if (context.model is ClassMappieCodeGenerationModel) {
            val models = context.model.mappings.values
                .filter { source -> source is ImplicitPropertyMappingSource && source.transformation is GeneratedViaMapperTransformation }
                .map { source -> (source as ImplicitPropertyMappingSource).transformation as GeneratedViaMapperTransformation }
                .distinctBy { it.source.type to it.target.type }
                .map { transformation ->
                    MappingResolver.of(
                        transformation.source.type.mappieType(),
                        transformation.target.type.mappieType(),
                        ResolverContext(context, context.definitions, context.model.declaration)
                    ).resolve(null).single()
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
