package tech.mappie.resolving

import org.jetbrains.kotlin.ir.declarations.*
import tech.mappie.resolving.classes.ClassResolver
import tech.mappie.resolving.classes.ObjectMappingSource
import tech.mappie.resolving.enums.EnumResolver
import org.jetbrains.kotlin.ir.symbols.IrConstructorSymbol
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.name.Name
import tech.mappie.BaseVisitor
import tech.mappie.api.*
import tech.mappie.generation.ShouldTransformCollector
import tech.mappie.mappieTerminate
import tech.mappie.resolving.classes.MappieVia
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
    val generated: Set<MappieVia>,
) : Mapping

data class EnumMapping(
    val targetType: IrType,
    val sourceType: IrType,
    val mappings: Map<IrEnumEntry, List<EnumMappingTarget>>,
) : Mapping

class MappingResolver : BaseVisitor<Map<IrFunction, List<Mapping>>, List<MappieDefinition>>(null) {

    override fun visitModuleFragment(declaration: IrModuleFragment, data: List<MappieDefinition>): Map<IrFunction, List<Mapping>> {
        return declaration.files.map { file -> file.accept(data) }
            .fold(mapOf(), Map<IrFunction, List<Mapping>>::plus)
    }

    override fun visitFile(declaration: IrFile, data: List<MappieDefinition>): Map<IrFunction, List<Mapping>> {
        return declaration.declarations
            .map { it.accept(data) }
            .fold(mapOf(), Map<IrFunction, List<Mapping>>::plus)
    }

    override fun visitClass(declaration: IrClass, data: List<MappieDefinition>): Map<IrFunction, List<Mapping>> {
        return if (declaration.accept(ShouldTransformCollector(declaration.fileEntry), Unit)) {
            declaration.declarations
                .map { it.accept(data) }
                .fold(mapOf(), Map<IrFunction, List<Mapping>>::plus)
        } else {
            emptyMap()
        }
    }

    override fun visitFunction(declaration: IrFunction, data: List<MappieDefinition>): Map<IrFunction, List<Mapping>> {
        return if (declaration.accept(ShouldTransformCollector(declaration.fileEntry), Unit)) {
            val type = declaration.parentAsClass
            return when {
                type.isStrictSubclassOf(EnumMappie::class) -> {
                    mapOf(declaration to listOf(EnumResolver(declaration).resolve()))
                }
                type.isStrictSubclassOf(ObjectMappie::class, ObjectMappie2::class, ObjectMappie3::class, ObjectMappie4::class, ObjectMappie5::class) -> {
                    mapOf(declaration to ClassResolver(declaration, data).resolve())
                }
                else -> {
                    mappieTerminate(
                        "Declaration ${declaration.name.asString()} is not a Mappie subclass",
                        location(declaration)
                    )
                }
            }
        } else {
            emptyMap()
        }
    }

    override fun visitProperty(declaration: IrProperty, data: List<MappieDefinition>): Map<IrFunction, List<Mapping>> {
        return emptyMap()
    }
}
