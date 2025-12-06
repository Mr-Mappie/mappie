package tech.mappie.ir

import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.classOrFail
import org.jetbrains.kotlin.ir.types.classifierOrFail
import org.jetbrains.kotlin.ir.types.makeNotNull
import org.jetbrains.kotlin.ir.types.typeOrFail
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.dumpKotlinLike
import org.jetbrains.kotlin.ir.util.functions
import org.jetbrains.kotlin.ir.util.isTypeParameter
import org.jetbrains.kotlin.ir.util.parents
import org.jetbrains.kotlin.ir.util.superClass
import tech.mappie.ir.generation.IrMappieGeneratedClass
import tech.mappie.ir.util.*
import tech.mappie.util.IDENTIFIER_IDENTITY_MAPPER

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
        if (source == target) {
             sequenceOf(context.definitions.definitions.first { it.clazz.name == IDENTIFIER_IDENTITY_MAPPER })
        } else {
            definitions.filter { mappie ->
                val isSubtype = source.makeNotNull().isSubtypeOf(mappie.source.erased(source)) && mappie.target.erased(target.makeNotNull()).isSubtypeOf(target)
                val isCorrectDefinitionScope = parent?.let { mappie.clazz !is IrMappieGeneratedClass && (it == mappie.clazz || it in mappie.clazz.parents) } ?: true
                isSubtype && isCorrectDefinitionScope
            }
        }

    private fun IrType.erased(container: IrType): IrType {
        return if (arguments.any { it.typeOrFail.isTypeParameter() }) {
            classOrFail.owner.typeWith(container.arguments.map { it.typeOrFail })
        } else {
            this
        }
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

    enum class Priority(value: Int) {
        EXACT_TYPE_MATCH(1),
        EXACT_CLASSIFIER_MATCH(2),
        TARGET_TYPE_MATCH(3),
        TARGET_CLASSIFIER_MATCH(4),
        SOURCE_TYPE_MATCH(5),
        SOURCE_CLASSIFIER_MATCH(6),
        NO_MATCH(7),
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
) : MappieDefinition {

    override val origin = this

    override fun toString() = "${clazz.name} ${source.dumpKotlinLike()} to ${target.dumpKotlinLike()}"

    companion object {
        context (context: MappieContext)
        fun of(clazz: IrClass): InternalMappieDefinition {
            val (source, target) = clazz.mappieSuperClassTypes()
            val parent = clazz.superClass?.let { parent ->
                if (allMappieClasses().any { parent.isSubclassOf(it) }) {
                    of(parent)
                } else {
                    null
                }
            }

            return InternalMappieDefinition(clazz, source, target, parent)
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

