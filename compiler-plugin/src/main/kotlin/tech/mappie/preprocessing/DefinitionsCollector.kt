package tech.mappie.preprocessing

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import tech.mappie.util.BaseVisitor
import tech.mappie.MappieContext
import tech.mappie.resolving.ResolverContext
import tech.mappie.api.Mappie
import tech.mappie.resolving.MappieDefinition
import tech.mappie.util.*

// TODO: we should collect al publicly visible, and add those during resolving that are visible from the current scope.
class DefinitionsCollector(val context: MappieContext) {
    fun collect(module: IrModuleFragment): ResolverContext {
        val builtin = BuiltinMappieDefinitionsCollector(context).collect()
        val defined = module.accept(ProjectMappieDefinitionsCollector(), Unit)
        return ResolverContext(context, builtin + defined)
    }
}

class BuiltinMappieDefinitionsCollector(val context: MappieContext) {
    fun collect() = MAPPERS
        .map { name -> context.pluginContext.referenceClass(ClassId(FqName(PACKAGE), Name.identifier(name)))!!.owner }
        .map { MappieDefinition(it) }

    companion object {
        private const val PACKAGE = "tech.mappie.api.builtin"
        private val MAPPERS = listOf(
            "LocalDateTimeToLocalTimeMapper",
            "LocalDateTimeToLocalDateMapper",
            "CharToStringMapper",
            "LongToBigIntegerMapper",
            "LongToBigDecimalMapper",
            "LongToStringMapper",
            "IntToLongMapper",
            "IntToBigIntegerMapper",
            "IntToBigDecimalMapper",
            "IntToStringMapper",
            "ShortToIntMapper",
            "ShortToLongMapper",
            "ShortToBigIntegerMapper",
            "ShortToBigDecimalMapper",
            "ShortToStringMapper",
            "ByteToShortMapper",
            "ByteToIntMapper",
            "ByteToLongMapper",
            "ByteToBigIntegerMapper",
            "ByteToBigDecimalMapper",
            "ByteToStringMapper",
            "FloatToDoubleMapper",
            "FloatToBigDecimalMapper",
            "FloatToStringMapper",
            "DoubleToBigDecimalMapper",
            "DoubleToStringMapper",
            "BigIntegerToStringMapper",
            "BigDecimalToStringMapper",
            "UUIDToStringMapper",
        )
    }
}

class ProjectMappieDefinitionsCollector : BaseVisitor<List<MappieDefinition>, Unit>() {

    override fun visitModuleFragment(declaration: IrModuleFragment, data: Unit) =
        declaration.files.flatMap { it.accept(data) }

    override fun visitFile(declaration: IrFile, data: Unit): List<MappieDefinition> {
        return declaration.declarations.flatMap { it.accept(data) }
    }

    override fun visitClass(declaration: IrClass, data: Unit) =
        buildList {
            if (declaration.isSubclassOf(Mappie::class)) {
                (declaration.superTypes.single() as? IrSimpleType)?.let {
                    add(MappieDefinition(declaration))
                }
            }
            addAll(declaration.declarations.filterIsInstance<IrClass>().flatMap { it.accept(data) })
        }

    override fun visitElement(element: IrElement, data: Unit) =
        emptyList<MappieDefinition>()
}