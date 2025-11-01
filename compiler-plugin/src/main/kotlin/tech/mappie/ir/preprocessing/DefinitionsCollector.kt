package tech.mappie.ir.preprocessing

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import tech.mappie.*
import tech.mappie.api.PredefinedMappieProvider
import tech.mappie.api.builtin.BuiltInMappieProvider
import tech.mappie.api.kotlinx.datetime.KotlinxDateTimeMappieProvider
import tech.mappie.config.MappieModule
import tech.mappie.ir.util.BaseVisitor
import tech.mappie.exceptions.MappiePanicException.Companion.panic
import tech.mappie.ir.resolving.MappieDefinition
import tech.mappie.ir.resolving.RequestResolverContext
import tech.mappie.ir.util.isSubclassOf
import tech.mappie.ir.util.mappieSuperType
import tech.mappie.ir.util.mappieType

// TODO: we should collect all publicly visible, and add those during resolving that are visible from the current scope.
class DefinitionsCollector(val context: MappieContext) {
    fun collect(module: IrModuleFragment): RequestResolverContext {
        val builtin = BuiltinMappieDefinitionsCollector(context).collect()
        val defined = module.accept(ProjectMappieDefinitionsCollector(context), Unit)
        return RequestResolverContext(context, builtin + defined)
    }
}

class BuiltinMappieDefinitionsCollector(val context: MappieContext) {
    fun collect() = providers().flatMap { provider ->
        buildList {
            addAll(provider.common)
            if (context.pluginContext.platform in JvmPlatforms.allJvmPlatforms) {
                addAll(provider.jvm)
            }
        }
    }.map { load(it) }.toList()

    private fun load(name: String) =
        context.pluginContext.referenceClass(ClassId.fromString(name))
            ?.owner
            ?.let { MappieDefinition(it) }
            ?: panic("Could not find registered mapper $name on classpath.")

    fun providers(): List<PredefinedMappieProvider> {
        return buildList {
            add(BuiltInMappieProvider())
            if (MappieModule.KOTLINX_DATETIME in context.configuration.modules) {
                add(KotlinxDateTimeMappieProvider())
            }
        }
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
                context(context) {
                    declaration.mappieSuperType()?.let {
                        add(MappieDefinition(declaration))
                    }
                }
            }
            addAll(declaration.declarations.filterIsInstance<IrClass>().flatMap { it.accept(data) })
        }

    override fun visitElement(element: IrElement, data: Unit) =
        emptyList<MappieDefinition>()
}