package tech.mappie.resolving

import org.jetbrains.kotlin.ir.IrFileEntry
import tech.mappie.resolving.classes.ClassResolver
import tech.mappie.resolving.classes.ObjectMappingSource
import tech.mappie.resolving.enums.EnumResolver
import org.jetbrains.kotlin.ir.declarations.IrEnumEntry
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.symbols.IrConstructorSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.Name
import tech.mappie.BaseVisitor
import tech.mappie.api.*
import tech.mappie.mappieTerminate
import tech.mappie.resolving.classes.targets.MappieTarget
import tech.mappie.resolving.enums.EnumMappingTarget
import tech.mappie.util.isStrictSubclassOf
import tech.mappie.util.location

sealed interface Mapping

data class ConstructorCallMapping(
    val targetType: IrType,
    val sourceTypes: List<IrType>,
    val symbol: IrConstructorSymbol,
    val mappings: Map<MappieTarget, List<ObjectMappingSource>>,
    val unknowns: Map<Name, List<ObjectMappingSource>>,
) : Mapping

data class EnumMapping(
    val targetType: IrType,
    val sourceType: IrType,
    val mappings: Map<IrEnumEntry, List<EnumMappingTarget>>,
) : Mapping

class MappingResolver(file: IrFileEntry) : BaseVisitor<List<Mapping>, List<MappieDefinition>>(file) {

    override fun visitFunction(declaration: IrFunction, data: List<MappieDefinition>): List<Mapping> {
        val type = declaration.parentAsClass
        return when {
            type.isStrictSubclassOf(EnumMappie::class) -> {
                listOf(EnumResolver(declaration).resolve())
            }
            type.isStrictSubclassOf(ObjectMappie::class, ObjectMappie2::class, ObjectMappie3::class, ObjectMappie4::class, ObjectMappie5::class) -> {
                ClassResolver(declaration, data).resolve()
            }
            else -> {
                mappieTerminate(
                    "Declaration ${declaration.name.asString()} is not a Mappie subclass",
                    location(declaration)
                )
            }
        }
    }
}
