package tech.mappie.ir

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classifierOrFail
import org.jetbrains.kotlin.ir.types.makeNotNull
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.superClass
import org.jetbrains.kotlin.name.Name
import tech.mappie.ir.preprocessing.LocalConversionMethodCollector
import tech.mappie.ir.util.*
import tech.mappie.util.IDENTIFIER_IDENTITY_MAPPER

data class MappieDefinitionCollection(
    val internal: List<InternalMappieDefinition>,
    val internalNonGenerated: List<InternalMappieDefinition>,
    val internalIncremental: List<InternalMappieDefinition>,
    val external: List<ExternalMappieDefinition>,
    val generated: MutableList<GeneratedMappieDefinition>,
) {
    val all: Sequence<MappieDefinition>
        get() = internal.asSequence() +
                internalNonGenerated.asSequence() +
                internalIncremental.asSequence() +
                generated.asSequence() +
                external.asSequence()

    constructor() : this(listOf(), listOf(), listOf(), listOf(), mutableListOf())

    context(context: MappieContext)
    fun matching(origin: InternalMappieDefinition, source: IrType, target: IrType): Sequence<MappieDefinition> =
        if (source == target) {
             sequenceOf(all.first { it.clazz.name == IDENTIFIER_IDENTITY_MAPPER })
        } else {
            all.filter { mappie ->
                val isSubtype = source.makeNotNull().isSubtypeOf(mappie.source.erased(source))
                        && mappie.target.makeNotNull().erased(target).isSubtypeOf(target)

                isSubtype && mappie.isGeneratedWithin(origin)
            }
        }

    fun named(name: Name, origin: InternalMappieDefinition): MappieDefinition =
        generated.single { it.clazz.name == name && it.isGeneratedWithin(origin) }

    private fun MappieDefinition.isGeneratedWithin(origin: InternalMappieDefinition): Boolean =
        this !is GeneratedMappieDefinition || this.origin == origin
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

        private fun priority(definition: MappieDefinition, source: IrType, target: IrType): Priority {
            val sourceTypeMatch = definition.source == source
            val targetTypeMatch = definition.target == target
            val sourceClassifierMatch = definition.source.classifierOrFail == source.type.classifierOrFail
            val targetClassifierMatch = definition.target.classifierOrFail == target.type.classifierOrFail

            return when {
                sourceTypeMatch && targetTypeMatch -> Priority.EXACT_TYPE_MATCH
                sourceClassifierMatch && targetClassifierMatch -> Priority.EXACT_CLASSIFIER_MATCH
                targetTypeMatch -> Priority.TARGET_TYPE_MATCH
                sourceClassifierMatch -> Priority.TARGET_CLASSIFIER_MATCH
                sourceTypeMatch -> Priority.SOURCE_TYPE_MATCH
                targetClassifierMatch -> Priority.SOURCE_CLASSIFIER_MATCH
                else -> Priority.NO_MATCH
            }
        }
    }

    enum class Priority {
        EXACT_TYPE_MATCH,
        EXACT_CLASSIFIER_MATCH,
        TARGET_TYPE_MATCH,
        TARGET_CLASSIFIER_MATCH,
        SOURCE_TYPE_MATCH,
        SOURCE_CLASSIFIER_MATCH,
        NO_MATCH,
    }
}

interface MappieDefinition {
    val origin: InternalMappieDefinition
    val clazz: IrClass
    val source: IrType
    val target: IrType

    fun referenceMapFunction() = clazz.functions.first { it.isMappieMapFunction() }
}

data class InternalMappieDefinition(
    override val clazz: IrClass,
    override val source: IrType,
    override val target: IrType,
    val parent: MappieDefinition?,
    val localConversions: LocalConversionMethodCollection,
) : MappieDefinition {

    override val origin = this

    override fun toString() = "${clazz.name} ${source.dumpKotlinLike()} to ${target.dumpKotlinLike()}"

    companion object {
        context (context: MappieContext)
        fun of(clazz: IrClass): InternalMappieDefinition {
            val (source, target) = clazz.typeWith().mappieSourceAndTarget()
            val parent = clazz.superClass?.let { parent ->
                if (allMappieClasses().any { parent.isSubclassOf(it) }) {
                    of(parent)
                } else {
                    null
                }
            }

            return InternalMappieDefinition(
                clazz,
                source,
                target,
                parent,
                LocalConversionMethodCollector.collect(clazz),
            )
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
            val (source, target) = clazz.typeWith().mappieSourceAndTarget()
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

