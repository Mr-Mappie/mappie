package tech.mappie.ir.preprocessing

import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import tech.mappie.api.PredefinedMappieProvider
import tech.mappie.exceptions.MappiePanicException.Companion.panic
import tech.mappie.ir.ExternalMappieDefinition
import tech.mappie.ir.MappieContext
import java.io.File
import java.net.URLClassLoader
import java.util.ServiceLoader

class ExternalDefinitionsCollector(val context: MappieContext) {

    context(context: MappieContext)
    fun collect(): List<ExternalMappieDefinition> = providers().flatMap { provider ->
        buildList {
            addAll(provider.common)
            if (context.pluginContext.platform in JvmPlatforms.allJvmPlatforms) {
                addAll(provider.jvm)
            }
        }
    }.map { load(it) }.toList()

    fun providers(): List<PredefinedMappieProvider> =
        URLClassLoader(
            context.configuration.classpath.map { File(it).toURI().toURL() }.toTypedArray(),
            PredefinedMappieProvider::class.java.classLoader,
        ).use { classLoader ->
            ServiceLoader.load(PredefinedMappieProvider::class.java, classLoader).toList()
        }

    context(context: MappieContext)
    private fun load(name: String) =
        context.pluginContext.finderForBuiltins().findClass(ClassId.fromString(name))
            ?.owner
            ?.let { ExternalMappieDefinition.of(it) }
            ?: panic("Could not find registered mapper $name on classpath.")
}
