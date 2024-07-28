package tech.mappie.resolving

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import tech.mappie.BaseVisitor
import tech.mappie.MappieIrRegistrar.Companion.context
import tech.mappie.api.Mappie
import tech.mappie.util.*

// TODO: we should collect al publicly visible, and add those during resolving that are visible from the current scope.
class MappieDefinitionsCollector {
    fun collect(module: IrModuleFragment) =
        MappieDefinitions(
            module.accept(ProjectMappieDefinitionsCollector(), Unit).toList() +
            BuiltinMappieDefinitionsCollector().collect()
        )
}

class BuiltinMappieDefinitionsCollector {
    fun collect() = MAPPERS
        .map { name -> context.referenceClass(ClassId(FqName(PACKAGE), Name.identifier(name)))!!.owner }
        .map { MappieDefinition(it) }

    companion object {
        private const val PACKAGE = "tech.mappie.api.builtin"
        private val MAPPERS = listOf(
            "LocalDateTimeToLocalTimeMapper",
            "LocalDateTimeToLocalDateMapper",
            "LongToBigIntegerMapper",
            "IntToLongMapper",
            "IntToBigIntegerMapper",
            "ShortToIntMapper",
            "ShortToLongMapper",
            "ShortToBigIntegerMapper",
            "ByteToShortMapper",
            "ByteToIntMapper",
            "ByteToLongMapper",
            "ByteToBigIntegerMapper",
        )
    }
}

class ProjectMappieDefinitionsCollector : BaseVisitor<List<MappieDefinition>, Unit>(null) {

    override fun visitModuleFragment(declaration: IrModuleFragment, data: Unit) =
        declaration.files.flatMap { it.accept(data) }

    override fun visitFile(declaration: IrFile, data: Unit): List<MappieDefinition> {
        file = declaration.fileEntry
        return declaration.declarations.flatMap { it.accept(data) }
    }

    override fun visitClass(declaration: IrClass, data: Unit) =
        buildList {
            if (declaration.isStrictSubclassOf(Mappie::class)) {
                (declaration.superTypes.single() as? IrSimpleType)?.let {
                    add(MappieDefinition(declaration))
                }
            }
            addAll(declaration.declarations.filterIsInstance<IrClass>().flatMap { it.accept(data) })
        }

    override fun visitElement(element: IrElement, data: Unit) =
        emptyList<MappieDefinition>()
}