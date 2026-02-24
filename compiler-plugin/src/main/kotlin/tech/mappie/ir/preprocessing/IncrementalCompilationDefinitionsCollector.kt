package tech.mappie.ir.preprocessing

import org.jetbrains.kotlin.ir.util.classId
import org.jetbrains.kotlin.name.ClassId
import tech.mappie.ir.InternalMappieDefinition
import tech.mappie.ir.MappieContext

class IncrementalCompilationDefinitionsCollector {

    context(context: MappieContext)
    fun collect(internal: List<InternalMappieDefinition>): List<InternalMappieDefinition> {
        return context.persistent.incremental.mapNotNull { classId ->
            context.pluginContext.referenceClass(classId)?.owner?.let { clazz ->
                if (internal.none { it.clazz.classId == clazz.classId }) {
                    clazz.let {
                        InternalMappieDefinition.of(clazz)
                    }
                } else {
                    null
                }
            }
        }
    }
}