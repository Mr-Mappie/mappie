package io.github.mappie.resolving

import io.github.mappie.BaseVisitor
import io.github.mappie.resolving.classes.ClassMappingResolver
import io.github.mappie.resolving.classes.MappingSource
import io.github.mappie.resolving.enums.EnumMappingResolver
import io.github.mappie.resolving.primitives.PrimitiveMappingResolver
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.types.isPrimitiveType
import org.jetbrains.kotlin.ir.types.isString
import org.jetbrains.kotlin.ir.util.*

sealed interface Mapping

data class ConstructorCallMapping(
    val targetType: IrType,
    val sourceType: IrType,
    val mappings: Map<IrValueParameter, List<MappingSource>>,
) : Mapping

data class EnumMapping(
    val targetType: IrType,
    val sourceType: IrType,
    val mappings: Map<IrEnumEntry, List<IrEnumEntry>>,
) : Mapping

data class SingleValueMapping(
    val type: IrType,
    val value: IrExpression,
) : Mapping

class MappingResolver
    : BaseVisitor<Mapping, Unit> {

    override fun visitFunction(declaration: IrFunction, data: Unit): Mapping {
        val type = declaration.returnType
        val clazz = type.getClass()!!
        return when {
            clazz.isEnumClass -> declaration.accept(EnumMappingResolver(), Unit)
            clazz.isData -> declaration.accept(ClassMappingResolver(), Unit)
            type.isPrimitiveType() || type.isString() -> declaration.accept(PrimitiveMappingResolver(), Unit)
            else -> error("Only mapping of data- and enum classes are supported yet.")
        }
    }
}
