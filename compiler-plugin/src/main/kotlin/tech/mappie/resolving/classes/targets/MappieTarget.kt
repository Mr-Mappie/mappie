package tech.mappie.resolving.classes.targets

import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.name.Name

sealed interface MappieTarget {
    val name: Name
    val type: IrType
}

data class MappieSetterTarget(val value: IrProperty) : MappieTarget {

    init {
        value.setter != null
    }

    override val name = value.name
    override val type = value.setter!!.valueParameters.first().type
}

data class MappieFunctionTarget(val value: IrSimpleFunctionSymbol) : MappieTarget {
    override val name = Name.identifier(value.owner.name.asString().removePrefix("set").replaceFirstChar { it.lowercaseChar() })
    override val type = value.owner.valueParameters.first().type
}

data class MappieValueParameterTarget(val value: IrValueParameter) : MappieTarget {
    override val name = value.name
    override val type = value.type
}