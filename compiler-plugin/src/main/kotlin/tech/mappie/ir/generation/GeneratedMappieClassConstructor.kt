package tech.mappie.ir.generation

import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.ir.builders.declarations.*
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.ir.builders.declarations.buildReceiverParameter
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.Name
import tech.mappie.ir.resolving.MappieDefinition
import tech.mappie.referenceEnumMappieClass
import tech.mappie.referenceObjectMappieClass
import tech.mappie.ir.resolving.MappingRequest
import tech.mappie.util.IDENTIFIER_MAP

class GeneratedMappieClassConstructor(
    private val context: CodeGenerationContext,
) {
    private val base = when (context.model) {
        is ClassMappieCodeGenerationModel -> context.referenceObjectMappieClass()
        is EnumMappieCodeGenerationModel -> context.referenceEnumMappieClass()
    }

    fun construct(request: MappingRequest, parent: IrDeclarationParent): Pair<CodeGenerationContext, IrClass>? {
        val existing = context.generated.entries.firstOrNull { it.key.first == request.source && it.key.second == request.target }
        return if (existing == null) {
            val model = CodeGenerationModelFactory.of(request).construct(context.model.declaration)
            val clazz = construct(parent, request)
            val context = context.copy(generated = context.generated + (request.source to request.target to clazz))
            val generated = clazz.transform(MappieCodeGenerator(context.copy(model = model)), null)
             context to (generated as IrClass)
        } else {
            null
        }
    }

    private fun construct(parent: IrDeclarationParent, request: MappingRequest): IrClass =
        context.pluginContext.irFactory.buildClass {
            name = name(request)
            kind = ClassKind.OBJECT
        }.also { clazz ->
            clazz.parent = parent
            clazz.thisReceiver = clazz.buildReceiverParameter {
                type = clazz.symbol.typeWithParameters(emptyList())
            }
            clazz.superTypes = listOf(base.owner.symbol.typeWith(request.source, request.target))

            clazz.addSimpleDelegatingConstructor(
                base.constructors.single().owner,
                context.pluginContext.irBuiltIns,
                true
            )

            base.functions.single { it.owner.name == IDENTIFIER_MAP }.let { function ->
                clazz.addFunction {
                    name = function.owner.name
                    returnType = function.owner.returnType
                    updateFrom(function.owner)
                }.apply {
                    overriddenSymbols = listOf(function)
                    isFakeOverride = function.owner.name != IDENTIFIER_MAP

                    parameters += buildReceiverParameter {
                        kind = IrParameterKind.DispatchReceiver
                        type = function.owner.dispatchReceiverParameter!!.type
                    }

                    function.owner.parameters.filter { it.kind == IrParameterKind.Regular }.forEach { parameter ->
                        addValueParameter {
                            name = parameter.name
                            type = (clazz.superTypes.first() as IrSimpleType).arguments.first().typeOrFail
                        }
                    }
                }
            }
        }

    private fun name(request: MappingRequest) =
        Name.identifier(request.source.classOrFail.owner.name.toString() + "To" + request.target.classOrFail.owner.name.toString() + "Mapper")
}