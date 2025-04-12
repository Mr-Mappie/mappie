package tech.mappie.ir.resolving.classes.targets

import org.jetbrains.kotlin.ir.declarations.IrProperty
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.name.Name

sealed interface ClassMappingTarget {
    val name: Name
    val type: IrType
    val required: Boolean
}

data class SetterTarget(val value: IrProperty, override val type: IrType) : ClassMappingTarget {

    init { value.setter != null }

    override val name = value.name
    override val required = false
}

data class FunctionCallTarget(val value: IrSimpleFunctionSymbol) : ClassMappingTarget {
    override val name = Name.identifier(value.owner.name.asString().removePrefix("set").replaceFirstChar { it.lowercaseChar() })
    override val type = value.owner.valueParameters.first().type
    override val required = false
}

data class ValueParameterTarget(val value: IrValueParameter, override val type: IrType) : ClassMappingTarget {
    override val name = value.name
    override val required = true
}