package tech.mappie.ir

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.ScopeWithIr
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.backend.jvm.functionByName
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.builders.*
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.expressions.IrBody
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.classOrFail
import org.jetbrains.kotlin.ir.types.typeOrFail
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.properties
import org.jetbrains.kotlin.ir.util.superTypes
import org.jetbrains.kotlin.name.ClassId
import tech.mappie.MappieState
import tech.mappie.fir.resolving.Mapping
import tech.mappie.ir_old.util.blockBody
import tech.mappie.ir_old.util.isMappieMapFunction

data class CodeGenerationContext(val pluginContext: IrPluginContext)

class MappieIrRegistrar(private val state: MappieState) : IrGenerationExtension {

    override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
        val context = CodeGenerationContext(pluginContext)
        val factory = MappieCodeGenerationModelFactory(context)

        state.models.forEach { (classId, mapping) ->
            val model = factory.construct(classId, mapping)

            val clazz = pluginContext.referenceClass(classId)
            clazz?.owner?.apply { transform(MappieTransformer(context, model), null) }
        }
    }
}

class MappieCodeGenerationModelFactory(val context: CodeGenerationContext) {
    fun construct(classId: ClassId, mapping: Mapping): CodeGenerationModel {
        val clazz = context.pluginContext.referenceClass(classId)!!
        val (sourceClass, targetClass) = ((clazz.superTypes().single().type as IrSimpleType).arguments.map { it.typeOrFail.classOrFail })
        val constructor = targetClass.constructors.single()

        val mappings = mapping.mappings.map { (target, source) ->
            val target = when (target) {
                is tech.mappie.fir.resolving.ValueParameterTarget -> {
                    ValueParameterTarget(
                        parameter = constructor.owner.parameters.find { it.name == target.parameter.name }!!,
                    )
                }
            }
            val source = when (source!!) {
                is tech.mappie.fir.resolving.PropertySource -> {
                    PropertySource(
                        receiver = clazz.functionByName("map").owner.parameters.single { it.kind == IrParameterKind.Regular },
                        property = sourceClass.owner.properties.first { it.name == source.property.name },
                    )
                }
            }
            target to source
        }

        return UserDefinedClassCodeGenerationModel(
            constructor.owner,
            mappings.toMap()
        )
    }
}

class MappieTransformer(val context: CodeGenerationContext, val model: CodeGenerationModel) : IrElementTransformerVoidWithContext() {

    private val generator = ClassBodyGenerator(context)

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
                    is UserDefinedClassCodeGenerationModel -> generator.construct(model)
                }
            }
        }
    }

    private fun IrElement.transform() =
        transform(this@MappieTransformer, null)
}

class ClassBodyGenerator(val context: CodeGenerationContext) {

    context(scope: ScopeWithIr)
    fun construct(model: UserDefinedClassCodeGenerationModel): IrBody {
        return context.pluginContext.blockBody(scope.scope) {
            val call = irCallConstructor(model.constructor.symbol, emptyList()).apply {
                model.mappings.forEach { (target, source) ->
                    constructArgument(source).let { argument ->
                        arguments[target.parameter.indexInParameters] = argument
                    }
                }
            }

            val variable = createTmpVariable(call)

            +irReturn(irGet(variable))
        }
    }

    fun IrBuilderWithScope.constructArgument(source: PropertySource): IrExpression {
        val getter = irCall(source.property.getter!!).apply {
            dispatchReceiver = irGet(source.receiver)
        }
        return getter
    }
}
