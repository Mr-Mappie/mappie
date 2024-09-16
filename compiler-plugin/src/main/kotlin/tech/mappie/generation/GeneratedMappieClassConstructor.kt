package tech.mappie.generation

import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.ir.builders.declarations.addFunction
import org.jetbrains.kotlin.ir.builders.declarations.addValueParameter
import org.jetbrains.kotlin.ir.builders.declarations.buildClass
import org.jetbrains.kotlin.ir.builders.declarations.buildReceiverParameter
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationParent
import org.jetbrains.kotlin.ir.types.classOrFail
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.types.typeWithParameters
import org.jetbrains.kotlin.ir.util.addSimpleDelegatingConstructor
import org.jetbrains.kotlin.ir.util.constructors
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.name.Name
import tech.mappie.generation.enums.EnumMappieCodeGenerationModelFactory
import tech.mappie.referenceEnumMappieClass
import tech.mappie.resolving.EnumMappingRequest
import tech.mappie.util.IDENTIFIER_MAP

class GeneratedMappieClassConstructor(
    private val context: CodeGenerationContext,
    private val requests: List<EnumMappingRequest>,
) {
    private val mappie = context.referenceEnumMappieClass()

    fun construct(parent: IrDeclarationParent): Map<EnumMappingRequest, IrClass> =
        requests.associateWith {
            val model = EnumMappieCodeGenerationModelFactory().construct(context.model.declaration, it)
            val context = CodeGenerationContext(context, model, context.definitions, context.generated)
            construct(parent, it).transform(MappieCodeGenerator(context), null) as IrClass
        }

    private fun construct(parent: IrDeclarationParent, request: EnumMappingRequest): IrClass =
        context.pluginContext.irFactory.buildClass {
            name = name(request)
            kind = ClassKind.OBJECT
        }.also {
            it.parent = parent
            it.thisReceiver = buildReceiverParameter(it, it.origin, it.symbol.typeWithParameters(emptyList()))
            it.superTypes = listOf(mappie.owner.symbol.typeWith(request.source, request.target))

            it.addSimpleDelegatingConstructor(
                mappie.constructors.single().owner,
                context.pluginContext.irBuiltIns,
                true
            )

            mappie.functions.forEach { function ->
                it.addFunction {
                    name = function.owner.name
                    returnType = function.owner.returnType
                    updateFrom(function.owner)
                }.apply {
                    dispatchReceiverParameter = function.owner.dispatchReceiverParameter
                    overriddenSymbols = listOf(function)
                    isFakeOverride = function.owner.name != IDENTIFIER_MAP
                    body = function.owner.body
                    function.owner.valueParameters.forEach { parameter ->
                        addValueParameter(parameter.name, parameter.type)
                    }
                }
            }
        }

    private fun name(request: EnumMappingRequest) =
        Name.identifier(request.source.classOrFail.owner.name.toString() + "To" + request.target.classOrFail.owner.name.toString() + "Mapper")
}