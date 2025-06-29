package tech.mappie.ir.preprocessing

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import tech.mappie.*
import tech.mappie.ir.util.BaseVisitor
import tech.mappie.exceptions.MappiePanicException.Companion.panic
import tech.mappie.ir.resolving.MappieDefinition
import tech.mappie.ir.resolving.RequestResolverContext
import tech.mappie.util.PACKAGE_TECH_MAPPIE_API_BUILTIN

// TODO: we should collect al publicly visible, and add those during resolving that are visible from the current scope.
class DefinitionsCollector(val context: MappieContext) {
    fun collect(module: IrModuleFragment): RequestResolverContext {
        val builtin = BuiltinMappieDefinitionsCollector(context).collect()
        val defined = module.accept(ProjectMappieDefinitionsCollector(context), Unit)
        return RequestResolverContext(context, builtin + defined)
    }
}

class BuiltinMappieDefinitionsCollector(val context: MappieContext) {
    fun collect() = mappers().map { name ->
        context.pluginContext
            .referenceClass(ClassId(PACKAGE_TECH_MAPPIE_API_BUILTIN, Name.identifier(name)))
            ?.owner
            ?.let { MappieDefinition(it) }
            ?: panic("Could not find mappie-api on classpath.")
    }

    private fun mappers(): List<String> = COMMON_MAPPERS + when (context.pluginContext.platform) {
        in JvmPlatforms.allJvmPlatforms -> JVM_MAPPERS
        else -> emptyList()
    }

    companion object {
        private val JVM_MAPPERS = listOf(
            "LocalDateTimeToLocalTimeMapper",
            "LocalDateTimeToLocalDateMapper",
            "ByteToBigIntegerMapper",
            "ByteToBigDecimalMapper",
            "ShortToBigIntegerMapper",
            "ShortToBigDecimalMapper",
            "IntToBigIntegerMapper",
            "IntToBigDecimalMapper",
            "LongToBigIntegerMapper",
            "LongToBigDecimalMapper",
            "FloatToBigDecimalMapper",
            "BigIntegerToStringMapper",
            "BigDecimalToStringMapper",
            "UUIDToStringMapper",
            "DoubleToBigDecimalMapper",
        )

        private val COMMON_MAPPERS = listOf(
            "CharToStringMapper",
            "LongToStringMapper",
            "IntToLongMapper",
            "IntToStringMapper",
            "ShortToIntMapper",
            "ShortToLongMapper",
            "ShortToStringMapper",
            "ByteToShortMapper",
            "ByteToIntMapper",
            "ByteToLongMapper",
            "ByteToStringMapper",
            "FloatToDoubleMapper",
            "FloatToStringMapper",
            "DoubleToStringMapper",
        )
    }
}

class ProjectMappieDefinitionsCollector(val context: MappieContext) : BaseVisitor<List<MappieDefinition>, Unit>() {

    override fun visitModuleFragment(declaration: IrModuleFragment, data: Unit) =
        declaration.files.flatMap { it.accept(data) }

    override fun visitFile(declaration: IrFile, data: Unit): List<MappieDefinition> {
        return declaration.declarations.flatMap { it.accept(data) }
    }

    override fun visitClass(declaration: IrClass, data: Unit) =
        buildList {
            if (context.shouldGenerateCode(declaration)) {
                (declaration.superTypes.single() as? IrSimpleType)?.let {
                    add(MappieDefinition(declaration))
                }
            }
            addAll(declaration.declarations.filterIsInstance<IrClass>().flatMap { it.accept(data) })
        }

    override fun visitElement(element: IrElement, data: Unit) =
        emptyList<MappieDefinition>()
}