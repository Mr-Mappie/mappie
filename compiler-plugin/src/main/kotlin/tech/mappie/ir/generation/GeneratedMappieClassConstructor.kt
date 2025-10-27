package tech.mappie.ir.generation

import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.ir.builders.declarations.addFunction
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildClass
import org.jetbrains.kotlin.ir.builders.declarations.buildReceiverParameter
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationParent
import org.jetbrains.kotlin.ir.declarations.IrParameterKind
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.typeOrFail
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.types.typeWithParameters
import org.jetbrains.kotlin.ir.util.addChild
import org.jetbrains.kotlin.ir.util.addSimpleDelegatingConstructor
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.functions
import tech.mappie.ir.MappieContext
import tech.mappie.ir.referenceEnumMappieClass
import tech.mappie.ir.referenceObjectMappieClass
import tech.mappie.ir.resolving.GeneratedMappieDefinition
import tech.mappie.util.IDENTIFIER_MAP

class GeneratedMappieClassConstructor {

    context (context: MappieContext)
    fun construct(parent: IrClass, definition: GeneratedMappieDefinition, request: CodeGenerationModel): GeneratedMappieDefinition =
        context.pluginContext.irFactory.buildClass {
            name = definition.clazz.name
            kind = ClassKind.OBJECT
        }.let { clazz ->
            val base = when (request) {
                is ClassMappieCodeGenerationModel -> context.referenceObjectMappieClass()
                is EnumMappieCodeGenerationModel -> context.referenceEnumMappieClass()
            }

            clazz.parent = parent
            clazz.thisReceiver = clazz.buildReceiverParameter {
                type = clazz.symbol.typeWithParameters(emptyList())
            }
            clazz.superTypes = listOf(base.owner.symbol.typeWith(definition.source, definition.target))

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

            parent.addChild(clazz)

            context.definitions.generated.apply {
                remove(definition)
                add(definition.copy(clazz = clazz))
            }

            definition.copy(clazz = clazz)
        }
}