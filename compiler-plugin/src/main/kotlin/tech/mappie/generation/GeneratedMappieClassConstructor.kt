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
import tech.mappie.referenceEnumMappieClass
import tech.mappie.referenceObjectMappieClass
import tech.mappie.resolving.MappingRequest
import tech.mappie.util.IDENTIFIER_MAP

class GeneratedMappieClassConstructor(
    private val context: CodeGenerationContext,
    private val requests: List<MappingRequest>,
) {
    private val base = when (context.model) {
        is ClassMappieCodeGenerationModel -> context.referenceObjectMappieClass()
        is EnumMappieCodeGenerationModel -> context.referenceEnumMappieClass()
    }

    fun construct(parent: IrDeclarationParent): Map<MappingRequest, IrClass> =
        requests.associateWith {
            val model = CodeGenerationModelFactory.of(it).construct(context.model.declaration)
            construct(parent, it).transform(MappieCodeGenerator(context.copy(model = model)), null) as IrClass
        }

    private fun construct(parent: IrDeclarationParent, request: MappingRequest): IrClass =
        context.pluginContext.irFactory.buildClass {
            name = name(request)
            kind = ClassKind.OBJECT
        }.also {
            it.parent = parent
            it.thisReceiver = buildReceiverParameter(it, it.origin, it.symbol.typeWithParameters(emptyList()))
            it.superTypes = listOf(base.owner.symbol.typeWith(request.source, request.target))

            it.addSimpleDelegatingConstructor(
                base.constructors.single().owner,
                context.pluginContext.irBuiltIns,
                true
            )

            base.functions.forEach { function ->
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

    private fun name(request: MappingRequest) =
        Name.identifier(request.source.classOrFail.owner.name.toString() + "To" + request.target.classOrFail.owner.name.toString() + "Mapper")
}