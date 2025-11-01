package tech.mappie.ir

import org.jetbrains.kotlin.backend.jvm.ir.upperBound
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classifierOrFail
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.parents
import tech.mappie.ir.generation.IrMappieGeneratedClass
import tech.mappie.ir.util.*

class MappieDefinitionCollection(
    val internal: MutableList<InternalMappieDefinition> = mutableListOf(),
    val external: MutableList<ExternalMappieDefinition> = mutableListOf(),
    val generated: MutableList<GeneratedMappieDefinition> = mutableListOf(),
) {
    val definitions: Sequence<MappieDefinition>
        get() = internal.asSequence() + generated.asSequence() + external.asSequence()

    fun load(other: MappieDefinitionCollection) {
        internal.addAll(other.internal)
        external.addAll(other.external)
        generated.addAll(other.generated)
    }

    context (context: MappieContext)
    fun matching(source: IrType, target: IrType, parent: IrClass? = null): Sequence<MappieDefinition> =
        definitions.filter { mappie ->
            val isSubtype = source.upperBound.isSubtypeOf(mappie.source.upperBound) && mappie.target.upperBound.isSubtypeOf(target.upperBound)
            val isCorrectParent = parent?.let { mappie.clazz !is IrMappieGeneratedClass && (it == mappie.clazz || it in mappie.clazz.parents) } ?: true
            isSubtype && isCorrectParent
        }

}

class PrioritizationMap private constructor(private val entries: Map<Priority, List<MappieDefinition>>) {

    val size = entries.values.sumOf { it.size }

    fun select(): MappieDefinition? {
        Priority.entries.forEach { priority ->
            val entry = entries[priority]
            if (!entry.isNullOrEmpty()) {
                val definition = entry.singleOrNull()
                if (definition != null) {
                    return definition
                }
            }
        }
        return null
    }

    companion object {
        fun Sequence<MappieDefinition>.prioritize(source: IrType, target: IrType): PrioritizationMap =
            PrioritizationMap(groupBy { priority(it, source, target) })

        // TODO: should account for nullability (?)
        private fun priority(definition: MappieDefinition, source: IrType, target: IrType): Priority {
            val sourceMatch = definition.source.classifierOrFail == source.type.classifierOrFail
            val targetMatch = definition.target.classifierOrFail == target.type.classifierOrFail
            return when {
                sourceMatch && targetMatch -> Priority.EXACT_MATCH
                targetMatch -> Priority.TARGET_MATCH
                sourceMatch -> Priority.SOURCE_MATCH
                else -> Priority.NO_MATCH
            }
        }
    }

    enum class Priority(value: Int) {
        EXACT_MATCH(1),
        TARGET_MATCH(2),
        SOURCE_MATCH(3),
        NO_MATCH(4),
    }
}

interface MappieDefinition {
    val origin: InternalMappieDefinition
    val clazz: IrClass
    val source: IrType
    val target: IrType

    fun referenceMapFunction() = clazz.functions.first { it.isMappieMapFunction() }
    fun referenceMapNullableFunction() = clazz.functions.first { it.isMappieMapNullableFunction() }
}

data class InternalMappieDefinition(
    override val clazz: IrClass,
    override val source: IrType,
    override val target: IrType,
) : MappieDefinition {

    override val origin = this

    override fun toString() = "${clazz.name} ${source.dumpKotlinLike()} to ${target.dumpKotlinLike()}"

    companion object {
        context (context: MappieContext)
        fun of(clazz: IrClass): InternalMappieDefinition {
            val (source, target) = clazz.mappieSuperClassTypes()
            return InternalMappieDefinition(clazz, source, target)
        }
    }
}

class ExternalMappieDefinition(
    override val clazz: IrClass,
    override val source: IrType,
    override val target: IrType,
) : MappieDefinition {

    override val origin
        get() = error("External mappie definition does not have an origin")

    override fun toString() = "${clazz.name} ${source.dumpKotlinLike()} to ${target.dumpKotlinLike()}"

    companion object {
        context (context: MappieContext)
        fun of(clazz: IrClass): ExternalMappieDefinition {
            val (source, target) = clazz.mappieSuperClassTypes()
            return ExternalMappieDefinition(clazz, source, target)
        }
    }
}

data class GeneratedMappieDefinition(
    override val origin: InternalMappieDefinition,
    override val clazz: IrClass,
    override val source: IrType,
    override val target: IrType
) : MappieDefinition {
    override fun toString() = "${clazz.name} ${source.dumpKotlinLike()} to ${target.dumpKotlinLike()}"
}

