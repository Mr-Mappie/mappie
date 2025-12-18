package tech.mappie.ir.generation

import org.jetbrains.kotlin.descriptors.ClassDescriptor
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.DescriptorVisibility
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.SourceElement
import org.jetbrains.kotlin.descriptors.ValueClassRepresentation
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrFactory
import org.jetbrains.kotlin.ir.declarations.IrTypeParameter
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.declarations.MetadataSource
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.makeNotNull
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.Name.identifier
import tech.mappie.ir.analysis.Problem.Companion.exception

class IrMappieGeneratedClass(override var name: Name) : IrClass() {
    @ObsoleteDescriptorBasedAPI
    override val descriptor: ClassDescriptor
        get() = throw exception("Cannot access property of IrMappieGeneratedClass \"$name\"")
    override val symbol: IrClassSymbol
        get() = throw exception("Cannot access property of IrMappieGeneratedClass \"$name\"")
    override var kind: ClassKind
        get() = throw exception("Cannot access property of IrMappieGeneratedClass \"$name\"")
        set(value) {}
    override var modality: Modality
        get() = throw exception("Cannot access property of IrMappieGeneratedClass \"$name\"")
        set(value) {}
    override var isCompanion: Boolean
        get() = throw exception("Cannot access property of IrMappieGeneratedClass \"$name\"")
        set(value) {}
    override var isInner: Boolean
        get() = throw exception("Cannot access property of IrMappieGeneratedClass \"$name\"")
        set(value) {}
    override var isData: Boolean
        get() = throw exception("Cannot access property of IrMappieGeneratedClass \"$name\"")
        set(value) {}
    override var isValue: Boolean
        get() = throw exception("Cannot access property of IrMappieGeneratedClass \"$name\"")
        set(value) {}
    override var isExpect: Boolean
        get() = throw exception("Cannot access property of IrMappieGeneratedClass \"$name\"")
        set(value) {}
    override var isFun: Boolean
        get() = throw exception("Cannot access property of IrMappieGeneratedClass \"$name\"")
        set(value) {}
    override var hasEnumEntries: Boolean
        get() = throw exception("Cannot access property of IrMappieGeneratedClass \"$name\"")
        set(value) {}
    override val source: SourceElement
        get() = throw exception("Cannot access property of IrMappieGeneratedClass \"$name\"")
    override var superTypes: List<IrType>
        get() = throw exception("Cannot access property of IrMappieGeneratedClass \"$name\"")
        set(value) {}
    override var thisReceiver: IrValueParameter?
        get() = throw exception("Cannot access property of IrMappieGeneratedClass \"$name\"")
        set(value) {}
    override var valueClassRepresentation: ValueClassRepresentation<IrSimpleType>?
        get() = throw exception("Cannot access property of IrMappieGeneratedClass \"$name\"")
        set(value) {}
    override var sealedSubclasses: List<IrClassSymbol>
        get() = throw exception("Cannot access property of IrMappieGeneratedClass \"$name\"")
        set(value) {}
    override var startOffset: Int
        get() = throw exception("Cannot access property of IrMappieGeneratedClass \"$name\"")
        set(value) {}
    override var endOffset: Int
        get() = throw exception("Cannot access property of IrMappieGeneratedClass \"$name\"")
        set(value) {}
    override var attributeOwnerId: IrElement
        get() = throw exception("Cannot access property of IrMappieGeneratedClass \"$name\"")
        set(value) {}
    override var origin: IrDeclarationOrigin
        get() = throw exception("Cannot access property of IrMappieGeneratedClass \"$name\"")
        set(value) {}
    override val factory: IrFactory
        get() = throw exception("Cannot access property of IrMappieGeneratedClass \"$name\"")
    override var annotations: List<IrConstructorCall>
        get() = throw exception("Cannot access property of IrMappieGeneratedClass \"$name\"")
        set(value) {}
    override var isExternal: Boolean
        get() = throw exception("Cannot access property of IrMappieGeneratedClass \"$name\"")
        set(value) {}
    override var visibility: DescriptorVisibility
        get() = throw exception("Cannot access property of IrMappieGeneratedClass \"$name\"")
        set(value) {}
    override var typeParameters: List<IrTypeParameter>
        get() = throw exception("Cannot access property of IrMappieGeneratedClass \"$name\"")
        set(value) {}

    @UnsafeDuringIrConstructionAPI
    override val declarations: MutableList<IrDeclaration>
        get() = throw exception("Cannot access property of IrMappieGeneratedClass \"$name\"")
    override var metadata: MetadataSource?
        get() = throw exception("Cannot access property of IrMappieGeneratedClass \"$name\"")
        set(value) {}

    companion object {
        fun named(source: IrType, target: IrType): IrClass {
            val source = source.identifier()
            val target  = target.identifier()
            return IrMappieGeneratedClass(identifier(source + "To" + target + "Mapper"))
        }

        private fun IrType.identifier() =
            makeNotNull()
                .dumpKotlinLike()
                .replace("<", "")
                .replace(">", "")
    }
}