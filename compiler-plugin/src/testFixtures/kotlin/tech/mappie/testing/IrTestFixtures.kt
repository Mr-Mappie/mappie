package tech.mappie.testing

import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.declarations.IrTypeParameter
import org.jetbrains.kotlin.ir.declarations.impl.IrFactoryImpl
import org.jetbrains.kotlin.ir.symbols.impl.IrClassSymbolImpl
import org.jetbrains.kotlin.ir.symbols.impl.IrTypeParameterSymbolImpl
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.impl.IrSimpleTypeImpl
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.Variance

object IrTestFixtures {

    fun createIrClass(
        name: String,
    ): IrClass =
        IrFactoryImpl.createClass(
            startOffset = -1,
            endOffset = -1,
            origin = IrDeclarationOrigin.DEFINED,
            name = Name.identifier(name),
            visibility = DescriptorVisibilities.PUBLIC,
            kind = ClassKind.CLASS,
            symbol = IrClassSymbolImpl(),
            modality = Modality.OPEN,
        )

    fun createIrSimpleType(
        clazz: IrClass,
    ): IrSimpleType =
        IrSimpleTypeImpl(clazz.symbol, hasQuestionMark = false, arguments = emptyList(), annotations = emptyList())

    fun createIrTypeParameter(
        name: String,
        variance: Variance = Variance.INVARIANT,
        isReified: Boolean = false,
    ): IrTypeParameter =
        IrFactoryImpl.createTypeParameter(
            startOffset = -1,
            endOffset = -1,
            origin = IrDeclarationOrigin.DEFINED,
            name = Name.identifier(name),
            index = 0,
            variance = variance,
            isReified = isReified,
            symbol = IrTypeParameterSymbolImpl()
        )
}