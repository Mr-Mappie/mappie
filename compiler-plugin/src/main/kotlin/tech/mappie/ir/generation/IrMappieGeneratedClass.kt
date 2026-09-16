package tech.mappie.ir.generation

import org.jetbrains.kotlin.backend.common.compilationException
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
import org.jetbrains.kotlin.ir.expressions.IrAnnotation
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.makeNotNull
import org.jetbrains.kotlin.ir.types.removeAnnotations
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.name.Name.identifier

class IrMappieGeneratedClass(override var name: Name) : IrClass() {
    @ObsoleteDescriptorBasedAPI
    override val descriptor: ClassDescriptor
        get() = compilationException("Cannot access property of IrMappieGeneratedClass '$name'", this)
    override val symbol: IrClassSymbol
        get() = compilationException("Cannot access property of IrMappieGeneratedClass '$name'", this)
    override var kind: ClassKind
        get() = compilationException("Cannot access property of IrMappieGeneratedClass '$name'", this)
        set(value) {}
    override var modality: Modality
        get() = compilationException("Cannot access property of IrMappieGeneratedClass '$name'", this)
        set(value) {}
    override var isCompanion: Boolean
        get() = compilationException("Cannot access property of IrMappieGeneratedClass '$name'", this)
        set(value) {}
    override var isInner: Boolean
        get() = compilationException("Cannot access property of IrMappieGeneratedClass '$name'", this)
        set(value) {}
    override var isData: Boolean
        get() = compilationException("Cannot access property of IrMappieGeneratedClass '$name'", this)
        set(value) {}
    override var isValue: Boolean
        get() = compilationException("Cannot access property of IrMappieGeneratedClass '$name'", this)
        set(value) {}
    override var isExpect: Boolean
        get() = compilationException("Cannot access property of IrMappieGeneratedClass '$name'", this)
        set(value) {}
    override var isFun: Boolean
        get() = compilationException("Cannot access property of IrMappieGeneratedClass '$name'", this)
        set(value) {}
    override var hasEnumEntries: Boolean
        get() = compilationException("Cannot access property of IrMappieGeneratedClass '$name'", this)
        set(value) {}
    override val source: SourceElement
        get() = compilationException("Cannot access property of IrMappieGeneratedClass '$name'", this)
    override var superTypes: List<IrType>
        get() = compilationException("Cannot access property of IrMappieGeneratedClass '$name'", this)
        set(value) {}
    override var thisReceiver: IrValueParameter?
        get() = compilationException("Cannot access property of IrMappieGeneratedClass '$name'", this)
        set(value) {}
    override var valueClassRepresentation: ValueClassRepresentation<IrSimpleType>?
        get() = compilationException("Cannot access property of IrMappieGeneratedClass '$name'", this)
        set(value) {}
    override var sealedSubclasses: List<IrClassSymbol>
        get() = compilationException("Cannot access property of IrMappieGeneratedClass '$name'", this)
        set(value) {}
    override var startOffset: Int
        get() = compilationException("Cannot access property of IrMappieGeneratedClass '$name'", this)
        set(value) {}
    override var endOffset: Int
        get() = compilationException("Cannot access property of IrMappieGeneratedClass '$name'", this)
        set(value) {}
    override var attributeOwnerId: IrElement
        get() = compilationException("Cannot access property of IrMappieGeneratedClass '$name'", this)
        set(value) {}
    override var origin: IrDeclarationOrigin
        get() = compilationException("Cannot access property of IrMappieGeneratedClass '$name'", this)
        set(value) {}
    override val factory: IrFactory
        get() = compilationException("Cannot access property of IrMappieGeneratedClass '$name'", this)
    override var annotations: List<IrAnnotation>
        get() = compilationException("Cannot access property of IrMappieGeneratedClass '$name'", this)
        set(value) {}
    override var isExternal: Boolean
        get() = compilationException("Cannot access property of IrMappieGeneratedClass '$name'", this)
        set(value) {}
    override var visibility: DescriptorVisibility
        get() = compilationException("Cannot access property of IrMappieGeneratedClass '$name'", this)
        set(value) {}
    override var typeParameters: List<IrTypeParameter>
        get() = compilationException("Cannot access property of IrMappieGeneratedClass '$name'", this)
        set(value) {}

    @UnsafeDuringIrConstructionAPI
    override val declarations: MutableList<IrDeclaration>
        get() = compilationException("Cannot access property of IrMappieGeneratedClass '$name'", this)
    override var metadata: MetadataSource?
        get() = compilationException("Cannot access property of IrMappieGeneratedClass '$name'", this)
        set(value) {}

    companion object {
        fun named(source: IrType, target: IrType): IrClass {
            val source = source.identifier()
            val target  = target.identifier()
            return IrMappieGeneratedClass(identifier(source + "To" + target + "Mapper"))
        }

        private fun IrType.identifier() =
            makeNotNull()
                .removeAnnotations()
                .dumpKotlinLike()
                .replace("<", "")
                .replace(">", "")
    }
}