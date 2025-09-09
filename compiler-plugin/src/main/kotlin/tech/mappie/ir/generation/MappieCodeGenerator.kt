package tech.mappie.ir.generation

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.Scope
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import tech.mappie.exceptions.MappieProblemException.Companion.fail
import tech.mappie.ir.generation.classes.ObjectMappieCodeGenerator
import tech.mappie.ir.generation.enums.EnumMappieCodeGenerator
import tech.mappie.ir.resolving.MappingResolver
import tech.mappie.ir.resolving.ResolverContext
import tech.mappie.ir.resolving.classes.sources.TransformableClassMappingSource
import tech.mappie.ir.selection.MappingSelector
import tech.mappie.ir.util.isMappieMapFunction
import tech.mappie.ir.util.location
import tech.mappie.ir.util.mappieType
import tech.mappie.util.IDENTIFIER_MAPPING

class MappieCodeGenerator(private val context: CodeGenerationContext) : IrElementTransformerVoidWithContext() {

    override fun visitClassNew(declaration: IrClass): IrStatement = declaration.apply {
        val context = if (context.model is ClassMappieCodeGenerationModel) {
            val models = context.model.mappings.values.asSequence()
                .filterIsInstance<TransformableClassMappingSource>()
                .mapNotNull { source -> source.selectGeneratedTransformationMapping() }
                .distinctBy { it.source.type to it.target.type }
                .map { transformation ->
                    val source = transformation.source.type.mappieType()
                    val target = transformation.target.type.mappieType()
                    val options = MappingResolver.of(
                        source,
                        target,
                        ResolverContext(context, context.definitions, context.model.declaration)
                    ).resolve(null)

                    MappingSelector.of(options).select()?.first ?: run {
                        context.fail(
                            "Failed to generate mapper from ${source.dumpKotlinLike()} to ${target.dumpKotlinLike()} which was incorrectly assumed to be valid.",
                            declaration,
                            location(declaration)
                        )
                    }
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
            if (body == null) {
                when (context.model) {
                    is EnumMappieCodeGenerationModel -> EnumMappieCodeGenerator(context, context.model).body(scope)
                    is ClassMappieCodeGenerationModel -> ObjectMappieCodeGenerator(context, context.model).body(scope)
                }
            } else {
                declaration.body!!.transform(MappieFunctionBodyTransformer(scope, context), null)
            }
        }
    }
}

class MappieFunctionBodyTransformer(private val scope: Scope, private val context: CodeGenerationContext) : IrElementTransformerVoidWithContext() {
    override fun visitCall(expression: IrCall): IrExpression {
        return when (expression.symbol.owner.name) {
            IDENTIFIER_MAPPING -> {
                when (context.model) {
                    is EnumMappieCodeGenerationModel -> EnumMappieCodeGenerator(context, context.model).lambda(scope)
                    is ClassMappieCodeGenerationModel -> ObjectMappieCodeGenerator(context, context.model).lambda(scope)
                }
            }
            else -> {
                expression.arguments.forEachIndexed { index, argument ->
                    expression.arguments[index] = argument?.transform(this, null)
                }
                expression
            }
        }
    }
}