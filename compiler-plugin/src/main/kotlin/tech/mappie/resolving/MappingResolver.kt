package tech.mappie.resolving

import tech.mappie.resolving.classes.ClassResolver
import tech.mappie.resolving.classes.ObjectMappingSource
import tech.mappie.resolving.enums.EnumResolver
import tech.mappie.resolving.primitives.PrimitiveResolver
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.IrConstructorSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.types.isPrimitiveType
import org.jetbrains.kotlin.ir.types.isString
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.Name
import tech.mappie.BaseVisitor
import tech.mappie.resolving.enums.EnumMappingTarget

sealed interface Mapping

data class ConstructorCallMapping(
    val targetType: IrType,
    val sourceType: IrType,
    val symbol: IrConstructorSymbol,
    val mappings: Map<IrValueParameter, List<ObjectMappingSource>>,
    val unknowns: Map<Name, List<ObjectMappingSource>>,
) : Mapping

data class EnumMapping(
    val targetType: IrType,
    val sourceType: IrType,
    val mappings: Map<IrEnumEntry, List<EnumMappingTarget>>,
) : Mapping

data class SingleValueMapping(
    val type: IrType,
    val value: IrExpression,
) : Mapping

class MappingResolver : BaseVisitor<List<Mapping>, List<MappieDefinition>>() {

    override fun visitFunction(declaration: IrFunction, data: List<MappieDefinition>): List<Mapping> {
        val type = declaration.returnType
        val clazz = type.getClass()!!
        return when {
            type.isPrimitiveType() || type.isString() -> listOf(PrimitiveResolver(declaration).resolve())
            clazz.isEnumClass -> listOf(EnumResolver(declaration).resolve())
            clazz.isClass -> ClassResolver(declaration, data).resolve()
            else -> error("Only mapping of data- and enum classes are supported yet.")
        }
    }
}
