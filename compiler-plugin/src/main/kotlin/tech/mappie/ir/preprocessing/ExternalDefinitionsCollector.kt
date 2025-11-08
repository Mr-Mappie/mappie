package tech.mappie.ir.preprocessing

import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import tech.mappie.ir.MappieContext
import tech.mappie.api.PredefinedMappieProvider
import tech.mappie.api.builtin.BuiltInMappieProvider
import tech.mappie.api.kotlinx.datetime.KotlinxDateTimeMappieProvider
import tech.mappie.config.MappieModule
import tech.mappie.exceptions.MappiePanicException
import tech.mappie.ir.ExternalMappieDefinition

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

    fun providers(): List<PredefinedMappieProvider> {
        return buildList {
            add(BuiltInMappieProvider())
            if (MappieModule.KOTLINX_DATETIME in context.configuration.modules) {
                add(KotlinxDateTimeMappieProvider())
            }
        }
    }

    context(context: MappieContext)
    private fun load(name: String) =
        context.pluginContext.referenceClass(ClassId.fromString(name))
            ?.owner
            ?.let { ExternalMappieDefinition.of(it) }
            ?: MappiePanicException.panic("Could not find registered mapper $name on classpath.")
}