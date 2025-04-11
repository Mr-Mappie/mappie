package tech.mappie.ir.generation

import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.symbols.impl.IrValueParameterSymbolImpl
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.SYNTHETIC_OFFSET
import org.jetbrains.kotlin.name.SpecialNames
import tech.mappie.MappieContext
import tech.mappie.ir.resolving.MappieDefinition

class CodeGenerationContext(
    context: MappieContext,
    val model: CodeGenerationModel,
    val definitions: List<MappieDefinition>,
    val generated: Map<Pair<IrType, IrType>, IrClass>,
) : MappieContext by context {

    fun copy(
        model: CodeGenerationModel = this.model,
        definitions: List<MappieDefinition> = this.definitions,
        generated: Map<Pair<IrType, IrType>, IrClass> = this.generated
    ) = CodeGenerationContext(this, model, definitions, generated)
}

fun CodeGenerationContext.createThisReceiver(type: IrType, parent: IrDeclarationParent) =
    pluginContext.irFactory.createValueParameter(
        startOffset = SYNTHETIC_OFFSET,
        endOffset = SYNTHETIC_OFFSET,
        origin = IrDeclarationOriginImpl("FUNCTION_INTERFACE_CLASS"),
        name = SpecialNames.THIS,
        type = type,
        isAssignable = false,
        symbol = IrValueParameterSymbolImpl(),
        varargElementType = null,
        isCrossinline = false,
        isNoinline = false,
        isHidden = false,
        kind = IrParameterKind.DispatchReceiver,
    ).apply {
        this.parent = parent
    }