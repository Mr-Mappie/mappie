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

class MappieCodeGenerator(private val context: CodeGenerationContext) : IrElementTransformerVoidWithContext() {

    override fun visitClassNew(declaration: IrClass): IrStatement = declaration.apply {
        // TODO: resolving of the following should happen in the factory
        // TODO: we should use MappingSelector instead
        val context = if (context.model is ClassMappieCodeGenerationModel) {
            val models = context.model.mappings.values.mapNotNull { source ->
                if (source is ImplicitPropertyMappingSource && source.transformation is GeneratedViaMapperTransformation) {
                    MappingResolver.of(
                        source.transformation.source.type,
                        source.transformation.target.type,
                        ResolverContext(context, context.definitions, context.model.declaration)
                    ).resolve(null).single()
                } else {
                    null
                }
            }

            val generated = GeneratedMappieClassConstructor(context, models).construct(declaration)
            generated.entries.fold(context) { context, (request, generated) ->
                declarations.add(generated)
                context.copy(generated = context.generated + (request.source to request.target to generated))
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
