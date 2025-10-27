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
import org.jetbrains.kotlin.name.Name

class IrLazyGeneratedClass(override var name: Name) : IrClass() {
    @ObsoleteDescriptorBasedAPI
    override val descriptor: ClassDescriptor
        get() = TODO("Not yet implemented")
    override val symbol: IrClassSymbol
        get() = TODO("Not yet implemented")
    override var kind: ClassKind
        get() = TODO("Not yet implemented")
        set(value) {}
    override var modality: Modality
        get() = TODO("Not yet implemented")
        set(value) {}
    override var isCompanion: Boolean
        get() = TODO("Not yet implemented")
        set(value) {}
    override var isInner: Boolean
        get() = TODO("Not yet implemented")
        set(value) {}
    override var isData: Boolean
        get() = TODO("Not yet implemented")
        set(value) {}
    override var isValue: Boolean
        get() = TODO("Not yet implemented")
        set(value) {}
    override var isExpect: Boolean
        get() = TODO("Not yet implemented")
        set(value) {}
    override var isFun: Boolean
        get() = TODO("Not yet implemented")
        set(value) {}
    override var hasEnumEntries: Boolean
        get() = TODO("Not yet implemented")
        set(value) {}
    override val source: SourceElement
        get() = TODO("Not yet implemented")
    override var superTypes: List<IrType>
        get() = TODO("Not yet implemented")
        set(value) {}
    override var thisReceiver: IrValueParameter?
        get() = TODO("Not yet implemented")
        set(value) {}
    override var valueClassRepresentation: ValueClassRepresentation<IrSimpleType>?
        get() = TODO("Not yet implemented")
        set(value) {}
    override var sealedSubclasses: List<IrClassSymbol>
        get() = TODO("Not yet implemented")
        set(value) {}
    override var startOffset: Int
        get() = TODO("Not yet implemented")
        set(value) {}
    override var endOffset: Int
        get() = TODO("Not yet implemented")
        set(value) {}
    override var attributeOwnerId: IrElement
        get() = TODO("Not yet implemented")
        set(value) {}
    override var origin: IrDeclarationOrigin
        get() = TODO("Not yet implemented")
        set(value) {}
    override val factory: IrFactory
        get() = TODO("Not yet implemented")
    override var annotations: List<IrConstructorCall>
        get() = TODO("Not yet implemented")
        set(value) {}
    override var isExternal: Boolean
        get() = TODO("Not yet implemented")
        set(value) {}
    override var visibility: DescriptorVisibility
        get() = TODO("Not yet implemented")
        set(value) {}
    override var typeParameters: List<IrTypeParameter>
        get() = TODO("Not yet implemented")
        set(value) {}

    @UnsafeDuringIrConstructionAPI
    override val declarations: MutableList<IrDeclaration>
        get() = TODO("Not yet implemented")
    override var metadata: MetadataSource?
        get() = TODO("Not yet implemented")
        set(value) {}
}